package com.example.todoapp.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.db.ToDoItemEntity
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.adapters.ToDoActionListener
import com.example.todoapp.ui.adapters.ToDoAdapter
import com.example.todoapp.util.factory
import com.example.todoapp.ui.viewmodels.HomeViewModel
import com.example.todoapp.util.Resource
import com.example.todoapp.util.SwipeGesture
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels { factory() }

    private lateinit var adapter: ToDoAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var navController: NavController

//    private lateinit var networkChangeReceiver: NetworkChangeReceiver


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {

            homeViewModel.counterToDo.collect {

                binding.doneText.text = "Done - ${it}"

            }

        }

        binding.swipe.setOnRefreshListener(this)
//        networkChangeReceiver = NetworkChangeReceiver()

        recyclerViewInit()
        floatButtonInit()
        hiddenButtonInit()

        return root

    }

//    override fun onStart() {
//        super.onStart()
//        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        requireActivity().registerReceiver(networkChangeReceiver, filter)
//
//        networkChangeReceiver.isConnected.observe(this, Observer { isConnected ->
//            homeViewModel.setInternetConnected(isConnected)
//        })
//
//        homeViewModel.isInternetConnected.observe(this, Observer { isConnected ->
//            if (isConnected) {
//
//                performAction()
//
//            }
//
//        })
//
//    }

//    override fun onStop() {
//        super.onStop()
//        requireActivity().unregisterReceiver(networkChangeReceiver)
//    }

    private fun performAction() {

        lifecycleScope.launch {
            homeViewModel.getSavedTasks().collect { items ->

                homeViewModel.postSavedData(items)

            }

        }
    }

    override fun onRefresh() {

        viewLifecycleOwner.lifecycleScope.launch {

            homeViewModel.updateTasks()
            homeViewModel.getSavedTasks().collect() {

                if (!binding.showHiddenButton.isChecked)
                {

                    adapter.toDoList = it
                    homeViewModel.setCounter(adapter.toDoList.filter { it.isComplete }.size)

                }
                else {

                    adapter.toDoList = it.filter { !it.isComplete }

                }

            }
        }

        binding.swipe.isRefreshing = false

    }


    private fun hiddenButtonInit() {

        binding.showHiddenButton.setOnCheckedChangeListener { buttonView, isChecked ->

            recyclerViewInit()

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

    } // correct

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    } // correct

    private fun recyclerViewInit() {

        recyclerView = _binding!!.taskRecycler

        adapter = ToDoAdapter(object : ToDoActionListener {

            override fun onToDoItemDelete(todoItem: ToDoItemEntity) {

                homeViewModel.deleteTask(todoItem.id, todoItem.isComplete)

            }

            override fun onEditTask(todoItem: ToDoItemEntity) {

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

            override fun onCheckTask(todoItem: ToDoItemEntity, isChecked : Boolean) {

                if (isChecked) {

                    val fragmentActivity = requireActivity()
                    (fragmentActivity as? MainActivity)?.party()

                    homeViewModel.incrementCounterToDo()

                } else {

                    homeViewModel.decrementCounterToDo()

                }

                todoItem.isComplete = isChecked
                homeViewModel.updateTask(todoItem)

            }

            override fun onLongClick(todoItem: ToDoItemEntity) {



            }

            override fun onToDoItemCopy(todoItem: ToDoItemEntity) {

                copyTextToClipboard(todoItem.text)

            }

            override fun onToDoItemInfo(todoItem: ToDoItemEntity) {

                openInfoFragment(todoItem)

            }

        })

        viewLifecycleOwner.lifecycleScope.launch {

            homeViewModel.getSavedTasks().collect {

                if (binding.showHiddenButton.isChecked) {

                    adapter.toDoList = it.filter { !it.isComplete } //РЕАЛИЗОВАТЬ ЧЕРЕЗ УТИЛ, СЛЫШИШЬ???????????????? НЕ ЗАБУДЬ!!!!!!!!!!!!!!!!!

                }
                else {

                    adapter.toDoList = it
                    homeViewModel.setCounter(adapter.toDoList.filter { it.isComplete }.size)

                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launch {

            homeViewModel.toDoList.collect { response ->

                when (response) {

                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {

                            if (binding.showHiddenButton.isChecked) {

                                adapter.toDoList = it.filter { !it.isComplete } //РЕАЛИЗОВАТЬ ЧЕРЕЗ УТИЛ, СЛЫШИШЬ???????????????? НЕ ЗАБУДЬ!!!!!!!!!!!!!!!!!

                            }
                            else {

                                adapter.toDoList = it
                                homeViewModel.setCounter(adapter.toDoList.filter { it.isComplete }.size)

                            }

                        }
                    }
                    is Resource.Error -> {

                        hideProgressBar()
                        response.message?.let {message ->

                            Log.e("HOME_FRAGMENT", "An error occurred: $message")

                        }

                    }
                    is Resource.Loading -> {

                        showProgressBar()

                    }
                }

            }


        }


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeToGesture(recyclerView)


    }

    private fun hideProgressBar() {

        binding.circularProgressBar.visibility = View.INVISIBLE

    }

    private fun showProgressBar() {

        binding.circularProgressBar.visibility = View.VISIBLE

    }


    private fun copyTextToClipboard(text: String) {

        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()

    } // correct


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

                val position=viewHolder.absoluteAdapterPosition

                var actionBtnTapped = false

                try {

                    when(direction) {

                        ItemTouchHelper.LEFT-> {

                            val removedElement : ToDoItemEntity = (itemRv!!.adapter as?
                                    ToDoAdapter)!!.toDoList[position]

                            homeViewModel.deleteTask(removedElement.id, removedElement.isComplete)

                            val snackBar = Snackbar.make(

                                this@HomeFragment.recyclerView, "Item Deleted", Snackbar.LENGTH_LONG

                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {

                                    super.onDismissed(transientBottomBar, event)

                                }
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {

                                        homeViewModel.addTask(removedElement)

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

                        ItemTouchHelper.RIGHT-> {

                            val informedFragment : ToDoItemEntity = (itemRv!!.adapter as?
                                    ToDoAdapter)!!.toDoList[position]

                            openInfoFragment(informedFragment)
                            adapter.notifyItemChanged(position)
                            actionBtnTapped = true

                        }

                    }

                }

                catch (e:Exception) {

                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)

    }


    fun openInfoFragment(item: ToDoItemEntity) {

        val bundle = Bundle()
        bundle.putParcelable("ITEM", item)

        val fragment = InfoFragment.newInstance(bundle)

        fragment.show(requireActivity().supportFragmentManager, "info!")

    }


}
