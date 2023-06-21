package com.example.todoapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.ui.util.factory
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import com.example.todoapp.ui.viewmodels.HomeViewModel
import java.util.Calendar
import java.util.Date


class EditAddFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val editAddViewModel: EditAddViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditBinding.inflate(inflater, container, false)

        val root: View = binding.root

        arguments?.let {

            editAddViewModel.setItemById(it.getString("TASK_ID").toString())
            editAddViewModel.setFlag(it.getInt("SAVE_OR_EDIT_FLAG"))

        }

        init()

        return root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                showCancelWarningDialog()

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

    }
    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }
    private fun init() {

        // в зависимости от того, хочет ли пользователь установить дедлайн -
        // показать и скрыть календарь

        binding.showCalendar.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {

                binding.myDeadlineDatePicker.visibility = View.VISIBLE

            } else {

                binding.myDeadlineDatePicker.visibility = View.GONE

            }

        }

        // заполнение данных по объекту из ViewModel

        val text = editAddViewModel.toDoItem.text

        binding.descriptionInput.setText(text)

        val importance = editAddViewModel.toDoItem.importance
        val deadline : Date? = editAddViewModel.toDoItem.deadline


        when (importance) {

            ToDoItem.Importance.LOW -> _binding!!.toggleButtonImportance
                .check(_binding!!.slowButton.id)
            ToDoItem.Importance.NORMAL -> _binding!!.toggleButtonImportance
                .check(_binding!!.normalButton.id)
            ToDoItem.Importance.URGENT -> _binding!!.toggleButtonImportance
                .check(_binding!!.urgentlyButton.id)

        }

        if (deadline != null) {

            binding.showCalendar.isChecked = true
            binding.myDeadlineDatePicker.visibility = View.VISIBLE

            val calendar = Calendar.getInstance()
            calendar.time = deadline

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            binding.myDeadlineDatePicker.updateDate(year, month, day)

        } else {

            binding.showCalendar.isChecked = false
            binding.myDeadlineDatePicker.visibility = View.GONE

        }


        // обработка нажатия на "Отмену"

        binding.cancelButton.setOnClickListener {

           showCancelWarningDialog()

        }

        // обработка нажатия на "Сохранить"

        binding.saveButton.setOnClickListener {

            if (editAddViewModel.saveOrCreateFlag == 1) { // сохранить старую заметку

                fillModel()
                showSaveWarningDialog()

            } else if (editAddViewModel.saveOrCreateFlag == 2) { // создать новую заметку

                fillModel()
                showNewSaveWarningDialog()

            }

        }

        // обработка нажатия на "Удалить"

        binding.removeButton.setOnClickListener {

            if (editAddViewModel.saveOrCreateFlag == 1) {

                showRemoveWarningDialog()

            } else if (editAddViewModel.saveOrCreateFlag == 2) {

                showCancelWarningDialog()

            }

        }

    }

    private fun showCancelWarningDialog() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Cancel")
            .setMessage("Are you sure you want to close the editor?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ ->

                Navigation.findNavController(binding.root).navigate(R.id.nav_home)

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()

            }

            .show()

    }

    private fun showSaveWarningDialog() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Save")
            .setMessage("Are you sure you want to save the task?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ ->

                editAddViewModel.saveTask()
                Navigation.findNavController(binding.root).navigate(R.id.nav_home)

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            .show()

    }

    private fun showRemoveWarningDialog() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Remove")
            .setMessage("Are you sure you want to remove the task?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ ->


                editAddViewModel.deleteTask()


                Navigation.findNavController(binding.root).navigate(R.id.nav_home)

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            .show()

    }

    private fun showNewSaveWarningDialog() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Save")
            .setMessage("Are you sure you want to save the new task?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ ->

                editAddViewModel.addTask()
                Navigation.findNavController(binding.root).navigate(R.id.nav_home)

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            .show()

    }

    private fun fillModel() {

        val creationDate = Calendar.getInstance().time

        val modificationDate = Calendar.getInstance().time

        var deadline: Date? = null

        if (binding.showCalendar.isChecked) {

            val calendar
                    : Calendar = Calendar.getInstance()
            calendar.set(
                binding.myDeadlineDatePicker.year,
                binding.myDeadlineDatePicker.month,
                binding.myDeadlineDatePicker.dayOfMonth
            )

            deadline = calendar.time
        }

       val taskText = binding.descriptionInput.text.toString()

        val importance = when (binding.toggleButtonImportance.checkedButtonId) {

            R.id.slowButton -> ToDoItem.Importance.LOW
            R.id.normalButton -> ToDoItem.Importance.NORMAL
            R.id.urgentlyButton -> ToDoItem.Importance.URGENT
            else -> ToDoItem.Importance.NORMAL

        }

        val newToDoItem : ToDoItem

        if (editAddViewModel.saveOrCreateFlag == 2) {

            newToDoItem = ToDoItem("0",
                taskText, importance, deadline, false,
                creationDate, null)
        } else {

            newToDoItem = ToDoItem(
                editAddViewModel.toDoItem.id,
                taskText, importance, deadline, editAddViewModel.toDoItem.isDone,
                editAddViewModel.toDoItem.creationDate, modificationDate
            )

        }

        editAddViewModel.setItemByObject(newToDoItem)

    }

    companion object {

        fun newInstance(bundle: Bundle): EditAddFragment {
            val fragment = EditAddFragment()
            fragment.arguments = bundle
            return fragment
        }

    }


}
