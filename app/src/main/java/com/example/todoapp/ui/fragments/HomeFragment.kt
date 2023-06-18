package com.example.todoapp.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.ui.util.OnItemListener
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.adapters.ToDoAdapter
import com.example.todoapp.ui.util.SwipeGesture
import com.example.todoapp.ui.viewmodels.HomeAndAddViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), OnItemListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel: HomeAndAddViewModel by activityViewModels()

    private lateinit var adapter: ToDoAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        recyclerViewInit()
        floatButtonInit()
        hiddenButtonInit()

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.toDoList.observe(viewLifecycleOwner) {

            toDoList -> adapter.setData(homeViewModel.toDoList.value!!)

        }

        homeViewModel.counterToDo.observe(viewLifecycleOwner) {

            binding.doneText.text = "Done - ${homeViewModel.counterToDo.value}"

        }

    }

    fun hiddenButtonInit() {

        binding.showHiddenButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                homeViewModel.toDoList.value?.let { it1 -> adapter.setData(it1.filter { !it.isDone }) }

            }
            else {

                adapter.setData(homeViewModel.toDoList.value!!)

            }

        }

    }

    fun floatButtonInit() {
        _binding!!.editAddFragmentButton.setOnClickListener {
         findNavController().navigate(R.id.nav_gallery)
            homeViewModel.setStateFlag(2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun recyclerViewInit() {
        recyclerView = _binding!!.taskRecycler

        adapter = ToDoAdapter(requireContext(), homeViewModel.toDoList.value!!, this)


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeToGesture(recyclerView)

        recyclerView.setOnCreateContextMenuListener { menu, v, menuInfo ->
            val inflater = (v.context as AppCompatActivity).menuInflater
            inflater.inflate(R.menu.context_menu, menu)
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.copy_option -> {

                copyTextToClipboard("Вставь сюда норм текст")
                return true
            }

            R.id.info_option -> {


               openInfoFragment()

                return true
            }

            else -> return super.onContextItemSelected(item)

        }
    }

    fun copyTextToClipboard(text: String) {

        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()

    }


    override fun onItemClick(position: Int) {
        val clickedItem = adapter.getItem(position)
        homeViewModel.setFilledModel(clickedItem)
        Navigation.findNavController(binding.root).navigate(R.id.nav_gallery)
        homeViewModel.setStateFlag(1)
    }


    private fun swipeToGesture(itemRv: RecyclerView?) {

        val swipeGesture=object : SwipeGesture(requireContext()) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val from_pos = viewHolder.absoluteAdapterPosition
                val to_pos = target.absoluteAdapterPosition

                homeViewModel.swapElementsToRepository(from_pos, to_pos)
                adapter.notifyItemMoved(from_pos, to_pos)

                return false

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position=viewHolder.absoluteAdapterPosition

                var actionBtnTapped = false

                try {

                    when(direction) {

                        ItemTouchHelper.LEFT-> {

                            homeViewModel.setFilledModel(homeViewModel.toDoList.value!![position])
                            homeViewModel.removeDataFromRepoByPosition(position)

                                adapter.notifyItemRemoved(position)

                            val snackBar = Snackbar.make(

                               this@HomeFragment.recyclerView, "Item Deleted", Snackbar.LENGTH_LONG

                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {

                                    super.onDismissed(transientBottomBar, event)

                                }
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {

                                        homeViewModel.backToTheRepo(position)
                                        adapter.notifyItemInserted(position)
                                        homeViewModel.removeFilledModel()

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

                            homeViewModel.toDoList.value?.get(position)
                                ?.let { homeViewModel.setFilledModel(it) }
                            openInfoFragment()
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

    override fun onCheckBoxClicked(position: Int, isChecked: Boolean) {

        if (isChecked) {

           val activ = requireActivity()
            (activ as? MainActivity)?.party()

            homeViewModel.incrementCounterToDo()

        } else {

            homeViewModel.decrementCounterToDo()

        }


        homeViewModel.setCheckStatusToRepo(position, isChecked)

    }

    fun openInfoFragment() {

        val fragment = InfoFragment()
        fragment.show(requireActivity().supportFragmentManager, "info!")

    }

    override fun longClickPrepare(position: Int) {

        homeViewModel.toDoList.value?.get(position)
            ?.let { homeViewModel.setFilledModel(it) }

    }

}