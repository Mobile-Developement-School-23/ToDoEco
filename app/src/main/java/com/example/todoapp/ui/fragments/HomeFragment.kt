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
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.data.ToDoListener
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.adapters.ToDoActionListener
import com.example.todoapp.ui.adapters.ToDoAdapter
import com.example.todoapp.ui.util.SwipeGesture
import com.example.todoapp.ui.util.factory
import com.example.todoapp.ui.viewmodels.HomeViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels { factory() }

    private lateinit var adapter: ToDoAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var navController: NavController

    private val toDoListener: ToDoListener = {
        adapter.toDoList = it }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        navController = findNavController()

        homeViewModel.counterToDo.observe(viewLifecycleOwner) {

            binding.doneText.text = "Done - ${homeViewModel.counterToDo.value}"

        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewInit()
        floatButtonInit()
        hiddenButtonInit()

        return root

    }

    fun hiddenButtonInit() {

        binding.showHiddenButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                homeViewModel.toDoList.value?.let { it1 -> adapter.toDoList = (it1.filter { !it.isDone }) }

            }
            else {

                adapter.toDoList = (homeViewModel.toDoList.value!!)

            }

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

            override fun onToDoItemDelete(todoItem: ToDoItem) {

                homeViewModel.removeDataFromRepo(todoItem)

            }

            override fun onEditTask(todoItem: ToDoItem) {

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

            override fun onCheckTask(todoItem: ToDoItem, isChecked : Boolean) {

                if (isChecked) {

                    val activ = requireActivity()
                    (activ as? MainActivity)?.party()

                    homeViewModel.incrementCounterToDo()

                } else {

                    homeViewModel.decrementCounterToDo()

                }

                homeViewModel.setCheckStatusToRepo(todoItem, isChecked)

            }

            override fun onLongClick(todoItem: ToDoItem) {



            }

            override fun onToDoItemCopy(todoItem: ToDoItem) {

                copyTextToClipboard(todoItem.text)

            }

            override fun onToDoItemInfo(todoItem: ToDoItem) {

                openInfoFragment(todoItem)

            }

        })

        homeViewModel.toDoList.observe(viewLifecycleOwner, Observer {

            if (binding.showHiddenButton.isChecked) {

                adapter.toDoList = it.filter { !it.isDone }

            }
            else {

                adapter.toDoList = it

            }

        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeToGesture(recyclerView)


    }


    fun copyTextToClipboard(text: String) {

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

                val from_pos = viewHolder.absoluteAdapterPosition
                val to_pos = target.absoluteAdapterPosition

                val firstObject = (itemRv!!.adapter as? ToDoAdapter)!!.toDoList[from_pos]
                val secondObject = (itemRv!!.adapter as? ToDoAdapter)!!.toDoList[to_pos]

                homeViewModel.swapElementsToRepository(firstObject, secondObject)

                return false

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position=viewHolder.absoluteAdapterPosition

                var actionBtnTapped = false

                try {

                    when(direction) {

                        ItemTouchHelper.LEFT-> {

                            val removedElement : ToDoItem = (itemRv!!.adapter as?
                                    ToDoAdapter)!!.toDoList[position]

                            homeViewModel.removeDataFromRepo(removedElement)

                            val snackBar = Snackbar.make(

                                this@HomeFragment.recyclerView, "Item Deleted", Snackbar.LENGTH_LONG

                            ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {

                                    super.onDismissed(transientBottomBar, event)

                                }
                                override fun onShown(transientBottomBar: Snackbar?) {
                                    transientBottomBar?.setAction("UNDO") {

                                        homeViewModel.backToTheRepository(removedElement)

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

                            val informedFragment : ToDoItem = (itemRv!!.adapter as?
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


    fun openInfoFragment(item: ToDoItem) {

        val bundle = Bundle()
        bundle.putInt("SAVE_OR_EDIT_FLAG", 1)
        Log.d("ID", item.id.toString())
        Log.d("LIST", homeViewModel.toDoList.value.toString())
        bundle.putString("TASK_ID_INFO", "${item.id}")

        val fragment = InfoFragment.newInstance(bundle)

        fragment.show(requireActivity().supportFragmentManager, "info!")

    }


}