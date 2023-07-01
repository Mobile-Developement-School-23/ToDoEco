package com.example.todoapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.api.request_response_data.ToDoItemResponse
import com.example.todoapp.db.ToDoItemEntity
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.util.factory
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.UUID


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

            editAddViewModel.setFlag(it.getInt("SAVE_OR_EDIT_FLAG"))
           setItemById(it.getString("TASK_ID").toString())

        }


        return root

    }

    override fun onResume() {

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

            } else
                Toast.makeText(context,"dgnjkdllxd", Toast.LENGTH_SHORT).show()

        }

        // обработка нажатия на "Удалить"

        binding.removeButton.setOnClickListener {

            if (editAddViewModel.saveOrCreateFlag == 1) {

                showRemoveWarningDialog()

            } else if (editAddViewModel.saveOrCreateFlag == 2) {

                showCancelWarningDialog()

            }

        }

        super.onResume()
    }

    private fun setItemById(id: String) {

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                editAddViewModel.setItemById(id)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        init()

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


        val text = editAddViewModel._toDoItem.text

        Log.d("ELEMENT_NEW_FRAGMENT", text)

        binding.descriptionInput.setText(text)

        val importance = editAddViewModel._toDoItem.importance
        val deadline : Long? = editAddViewModel._toDoItem.dateDeadline


        when (importance) {

            ToDoItemResponse.Importance.low -> _binding!!.toggleButtonImportance
                .check(_binding!!.slowButton.id)
            ToDoItemResponse.Importance.basic -> _binding!!.toggleButtonImportance
                .check(_binding!!.normalButton.id)
            ToDoItemResponse.Importance.important -> _binding!!.toggleButtonImportance
                .check(_binding!!.urgentlyButton.id)

        }

        if (deadline != null) {

            binding.showCalendar.isChecked = true
            binding.myDeadlineDatePicker.visibility = View.VISIBLE

            val calendar = Calendar.getInstance()
            calendar.time = Date()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            binding.myDeadlineDatePicker.updateDate(year, month, day)

        } else {

            binding.showCalendar.isChecked = false
            binding.myDeadlineDatePicker.visibility = View.GONE

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

                saveTask()

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

                deleteTask()

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

                addTask()

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            .show()

    }

    private fun fillModel() {

        val creationDate : Long = Calendar.getInstance().time.time

        var deadline: Long? = null

        if (binding.showCalendar.isChecked) {

            val calendar
                    : Calendar = Calendar.getInstance()
            calendar.set(
                binding.myDeadlineDatePicker.year,
                binding.myDeadlineDatePicker.month,
                binding.myDeadlineDatePicker.dayOfMonth
            )

            deadline = calendar.time.time

        }

       val taskText = binding.descriptionInput.text.toString()

        val importance = when (binding.toggleButtonImportance.checkedButtonId) {

            R.id.slowButton -> ToDoItemResponse.Importance.low
            R.id.normalButton -> ToDoItemResponse.Importance.basic
            R.id.urgentlyButton -> ToDoItemResponse.Importance.important
            else -> ToDoItemResponse.Importance.basic

        }

        val newToDoItem : ToDoItemEntity

        if (editAddViewModel.saveOrCreateFlag == 2) {

            newToDoItem = ToDoItemEntity(UUID.randomUUID().toString(),
                taskText, importance, deadline, false, "color",
                creationDate, creationDate)

        } else {

            newToDoItem = ToDoItemEntity(
                editAddViewModel.toDoItem.id,
                taskText, importance, deadline, editAddViewModel.toDoItem.isComplete, "color",
                editAddViewModel.toDoItem.dateCreation, creationDate
            )

        }

        editAddViewModel.setItemByObject(newToDoItem)

    }

    private fun saveTask() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                editAddViewModel.saveTask()
            }
            Navigation.findNavController(binding.root).navigate(R.id.nav_home)
        }
    }

    private fun deleteTask() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                editAddViewModel.deleteTask()
            }
            Navigation.findNavController(binding.root).navigate(R.id.nav_home)
        }
    }

    private fun addTask() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                editAddViewModel.addTask()
            }
            Navigation.findNavController(binding.root).navigate(R.id.nav_home)
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): EditAddFragment {
            val fragment = EditAddFragment()
            fragment.arguments = bundle
            return fragment
        }

    }

}
