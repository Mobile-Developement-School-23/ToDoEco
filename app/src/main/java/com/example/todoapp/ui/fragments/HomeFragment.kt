package com.example.todoapp.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.todoapp.R
import com.example.todoapp.ToDoApplication
import com.example.todoapp.data.network.observers.ConnectivityObserver
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.UiState
import com.example.todoapp.ui.recycler.adapters.ToDoActionListener
import com.example.todoapp.ui.recycler.adapters.ToDoAdapter
import com.example.todoapp.ui.viewmodels.HomeViewModel
import com.example.todoapp.ui.viewmodels.ViewModelFactory
import com.example.todoapp.ui.recycler.SwipeGesture
import com.example.todoapp.ui.util.snackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var modelFactory: ViewModelFactory
    private val model: HomeViewModel by lazy {
        ViewModelProvider(this, modelFactory)[HomeViewModel::class.java]
    }
    private val visibility: StateFlow<Boolean> by lazy {
        model.visibility
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ToDoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var navController: NavController
    private var internetState = ConnectivityObserver.Status.Unavailable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as ToDoApplication).appComponent.inject(this)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        navController = findNavController()
        viewLifecycleOwner.lifecycleScope.launch {
            model.doneCounter.collect { count ->
                updateUI(count)
            }
        }
        lifecycleScope.launch {
            model.status.collectLatest {
                updateState(it)
            }
        }
        binding.swipe.setOnRefreshListener(this)
        recyclerViewInit()
        floatButtonInit()
        hiddenButtonInit()
        return root
    }

    private fun updateState(status: ConnectivityObserver.Status) {
        when (status) {
            ConnectivityObserver.Status.Available -> {
                if (internetState != status) {
                    view?.snackbar("Internet connection is available!")
                    lifecycle.coroutineScope.launch(Dispatchers.IO) {
                        model.merge().collect {
                            when (it) {
                                is UiState.Start -> view?.snackbar("Loading...")
                                is UiState.Success -> view?.snackbar("UP-TO-DATE!")
                                is UiState.Error -> view?.snackbar("Refreshing error, try again!")
                            }
                        }
                    }
                }

            }

            ConnectivityObserver.Status.Unavailable -> {
                if (internetState != status) {
                    view?.snackbar("Not internet connection!")
                }
            }

            ConnectivityObserver.Status.Losing -> {
                if (internetState != status) {
                    view?.snackbar("Loss of Internet connection...")
                }
            }

            ConnectivityObserver.Status.Lost -> {
                if (internetState != status) {
                    view?.snackbar("The Internet connection is lost.")
                }
            }
        }
        internetState = status
    }

    private fun updateUI(doneCounter: Int) {
        binding.doneText.text = "Done: $doneCounter"
    }
    override fun onRefresh() {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            model.merge().collect {
                when (it) {
                    is UiState.Start -> view?.snackbar("Loading...")
                    is UiState.Success -> view?.snackbar("UP-TO-DATE!")
                    is UiState.Error -> view?.snackbar("Refreshing error, try again!")
                }
            }
        }
        binding.swipe.isRefreshing = false
    }
    private fun hiddenButtonInit() {
        binding.showHiddenButton.setOnClickListener {
            model.invertVisibilityState()
        }
    }
    fun floatButtonInit() {
        _binding!!.editAddFragmentButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("SAVE_OR_EDIT_FLAG", 2)
            bundle.putString("TASK_ID", "-1")
            val fragment = EditAddFragment.newInstance(bundle)
            val builder = NavOptions.Builder()
            val navOptions: NavOptions =
                builder.setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                    .build()
            navController.navigate(R.id.nav_gallery, bundle, navOptions)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun recyclerViewInit() {
        recyclerView = _binding!!.taskRecycler
        adapter = ToDoAdapter(object : ToDoActionListener {
            override fun onToDoItemDelete(todoItem: TaskModel) {
                lifecycle.coroutineScope.launch(Dispatchers.Main) {
                    model.removeTask(todoItem).collect { uiState ->
                        when (uiState) {
                            is UiState.Error -> view?.snackbar(uiState.cause)
                            else -> {}
                        }
                    }
                }
            }
            override fun onEditTask(todoItem: TaskModel) {
                val bundle = Bundle()
                bundle.putInt("SAVE_OR_EDIT_FLAG", 1)
                bundle.putString("TASK_ID", "${todoItem.id}")
                val fragment = EditAddFragment.newInstance(bundle)
                val builder = NavOptions.Builder()
                val navOptions: NavOptions =
                    builder.setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                        .build()
                navController.navigate(R.id.nav_gallery, bundle, navOptions)
            }
            override fun onCheckTask(todoItem: TaskModel, isChecked: Boolean) {
                var changed = todoItem.copy(isDone = isChecked)
                lifecycleScope.launch(Dispatchers.Main) {
                    model.setTask(changed).collect { uiState ->
                        when (uiState) {
                            is UiState.Error -> {}
                            else -> {}
                        }
                    }
                }
            }
            override fun onLongClick(todoItem: TaskModel) {}
            override fun onToDoItemCopy(todoItem: TaskModel) {
                copyTextToClipboard(todoItem.text)
            }
            override fun onToDoItemInfo(todoItem: TaskModel) {
                openInfoFragment(todoItem)
            }
        })
        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            visibility.collectLatest { visibilityState ->
                when (visibilityState) {
                    true -> {
                        model.allTasks.collectLatest { uiState ->
                            when (uiState) {
                                is UiState.Success -> adapter.toDoList = uiState.data
                                is UiState.Error -> view?.snackbar(uiState.cause)
                                is UiState.Start -> adapter.toDoList = listOf()
                            }
                        }
                    }
                    false -> {
                        model.undoneTasks.collectLatest { uiState ->
                            when (uiState) {
                                is UiState.Success -> adapter.toDoList = uiState.data
                                is UiState.Error -> view?.snackbar(uiState.cause)
                                is UiState.Start -> adapter.toDoList = listOf()
                            }
                        }
                    }
                }
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        swipeToGesture(recyclerView)
    }
    private fun copyTextToClipboard(text: String) {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    private fun swipeToGesture(itemRv: RecyclerView?) {
        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                var actionBtnTapped = false
                try {
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            val removedElement: TaskModel = (itemRv!!.adapter as?
                                    ToDoAdapter)!!.toDoList[position]
                            lifecycle.coroutineScope.launch(Dispatchers.Main) {
                                model.removeTask(removedElement).collect { uiState ->
                                    when (uiState) {
                                        is UiState.Error -> view?.snackbar(uiState.cause)
                                        else -> {}
                                    }
                                }
                            }
                            val snackBar = Snackbar.make(
                                this@HomeFragment.recyclerView, "Item Deleted", Snackbar.LENGTH_LONG
                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                }
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {
                                        lifecycle.coroutineScope.launch {
                                            model.addTask(removedElement.text,
                                                removedElement.priority,
                                                removedElement.deadline).collect { uiState ->
                                                when (uiState) {
                                                    is UiState.Success -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                                                    is UiState.Error -> {
                                                        Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                                                    }
                                                    else -> {}
                                                }
                                            }
                                        }
                                        actionBtnTapped = true
                                    }
                                    super.onShown(transientBottomBar)
                                }
                            }).apply {
                                animationMode = Snackbar.ANIMATION_MODE_FADE
                            }
                            snackBar.setActionTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                            snackBar.show()
                        }
                        ItemTouchHelper.RIGHT -> {
                            val informedFragment: TaskModel = (itemRv!!.adapter as?
                                    ToDoAdapter)!!.toDoList[position]
                            openInfoFragment(informedFragment)
                            adapter.notifyItemChanged(position)
                            actionBtnTapped = true
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)
    }

    fun openInfoFragment(item: TaskModel) {
        val bundle = Bundle()
        bundle.putParcelable("ITEM", item)
        val fragment = InfoFragment.newInstance(bundle)
        fragment.show(requireActivity().supportFragmentManager, "info!")
    }
}
