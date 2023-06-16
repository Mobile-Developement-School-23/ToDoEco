package com.example.todoapp.ui.editor

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todoapp.R
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.home.HomeAndAddViewModel
import java.util.Calendar
import java.util.Date


class EditAddFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    private val binding get() = _binding!!

    private val homeAndAddViewModel: HomeAndAddViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditBinding.inflate(inflater, container, false)

        val root: View = binding.root
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

        val text = homeAndAddViewModel.getFilledModel().text

        binding.descriptionInput.setText(text)

        val importance = homeAndAddViewModel.getFilledModel().importance
        val deadline : Date? = homeAndAddViewModel.getFilledModel().deadline


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



            if (homeAndAddViewModel.getStateFlag() == 1) {

                fillOldModel()
                showSaveWarningDialog()

            } else if (homeAndAddViewModel.getStateFlag() == 2) {

                fillNewModel()
                showNewSaveWarningDialog()

            }

        }

        // обработка нажатия на "Удалить"

        binding.removeButton.setOnClickListener {

            if (homeAndAddViewModel.getStateFlag() == 1) {

                showRemoveWarningDialog()

            } else if (homeAndAddViewModel.getStateFlag() == 2) {

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

                homeAndAddViewModel.removeFilledModel()
                requireActivity().supportFragmentManager.popBackStack()

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

                homeAndAddViewModel.saveDataToRepo()
                homeAndAddViewModel.removeFilledModel()
                requireActivity().supportFragmentManager.popBackStack()

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

                homeAndAddViewModel.removeDataFromRepo()
                homeAndAddViewModel.removeFilledModel()
                requireActivity().supportFragmentManager.popBackStack()

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

                homeAndAddViewModel.addDataToRepo()
                Log.d("ADDATATOREPO", homeAndAddViewModel.toDoList.value.toString())
                homeAndAddViewModel.nextId()
                homeAndAddViewModel.removeFilledModel()
                requireActivity().supportFragmentManager.popBackStack()

            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            .show()

    }

    private fun fillOldModel() {

        // id не меняется, как и дата создания

        val modificationDate = Calendar.getInstance().time

        val calendar : Calendar = Calendar.getInstance()
        calendar.set(binding.myDeadlineDatePicker.year,
            binding.myDeadlineDatePicker.month,
            binding.myDeadlineDatePicker.dayOfMonth)

        val deadline : Date = calendar.time

       val taskText = binding.descriptionInput.text.toString()

        val importance = when (binding.toggleButtonImportance.checkedButtonId) {

            R.id.slowButton -> ToDoItem.Importance.LOW
            R.id.normalButton -> ToDoItem.Importance.NORMAL
            R.id.urgentlyButton -> ToDoItem.Importance.URGENT
            else -> ToDoItem.Importance.NORMAL

        }

        val newToDOItem : ToDoItem = ToDoItem(homeAndAddViewModel.getFilledModel().id,
            taskText, importance, deadline, homeAndAddViewModel.getFilledModel().isDone,
            homeAndAddViewModel.getFilledModel().creationDate, modificationDate)

        homeAndAddViewModel.setFilledModel(newToDOItem)

    }

    private fun fillNewModel() {

        val creationDate = Calendar.getInstance().time

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

        val newToDOItem : ToDoItem = ToDoItem(homeAndAddViewModel.getFilledModel().id,
            taskText, importance, deadline, false,
            creationDate, homeAndAddViewModel.getFilledModel().modificationDate)

        homeAndAddViewModel.setFilledModel(newToDOItem)

    }

}
