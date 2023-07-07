package com.example.todoapp.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.ToDoApplication
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.UiState
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import com.example.todoapp.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject


class EditAddFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var modelFactory: ViewModelFactory
    private val editAddViewModel: EditAddViewModel by lazy {
        ViewModelProvider(this, modelFactory)[EditAddViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as ToDoApplication).appComponent.inject(this)
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        arguments?.let {
            editAddViewModel.setFlag(it.getInt("SAVE_OR_EDIT_FLAG"))
            Log.d("О ПРИВЕТ 2", it.getString("TASK_ID").toString())
           setItemById(it.getString("TASK_ID").toString())
        }
        return root
    }

    override fun onResume() {

        // обработка нажатия на "Отмену"

        binding.cancelButton.setOnClickListener {
            animation(binding.cancelButton)
            showCancelWarningDialog()
        }

        // обработка нажатия на "Сохранить"

        binding.saveButton.setOnClickListener {
            animation(binding.saveButton)
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
            animation(binding.removeButton)
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
            animation(binding.cancelButton)
            showCancelWarningDialog()
        }

        // обработка нажатия на "Сохранить"

        binding.saveButton.setOnClickListener {
            animation(binding.saveButton)
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
            animation(binding.removeButton)
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


        val text = editAddViewModel.toDoItem.text
        Log.d("ELEMENT_NEW_FRAGMENT", text)
        binding.descriptionInput.setText(text)
        val importance = editAddViewModel.toDoItem.priority
        val deadline : Long? = editAddViewModel.toDoItem.deadline
        when (importance) {
            Importance.LOW -> _binding!!.toggleButtonImportance
                .check(_binding!!.slowButton.id)
            Importance.BASIC -> _binding!!.toggleButtonImportance
                .check(_binding!!.normalButton.id)
            Importance.IMPORTANT -> _binding!!.toggleButtonImportance
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
            .setPositiveButton("OK") { _, _ ->
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
            .setPositiveButton("OK") { _, _ ->
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
            R.id.slowButton -> Importance.LOW
            R.id.normalButton -> Importance.BASIC
            R.id.urgentlyButton -> Importance.IMPORTANT
            else -> Importance.BASIC
        }
        val newToDoItem : TaskModel = if (editAddViewModel.saveOrCreateFlag == 2) {
            TaskModel(UUID.randomUUID(), text = taskText,
                priority = importance, isDone = false,
                creationTime = Date().time, deadline = deadline,
                modifyingTime = Date().time)
        } else {
            TaskModel(editAddViewModel.toDoItem.id, text = taskText,
                priority = importance, isDone = editAddViewModel.toDoItem.isDone,
                creationTime = editAddViewModel.toDoItem.creationTime, deadline = deadline,
                modifyingTime = Date().time)
        }
        editAddViewModel.setItemByObject(newToDoItem)
    }

    private fun saveTask() {
        lifecycle.coroutineScope.launch {
            editAddViewModel.setTask().collect { uiState: UiState<String> ->
                when (uiState) {
                    is UiState.Start -> {}
                    is UiState.Success -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    is UiState.Error -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    }
                }
            }
    }


    private fun deleteTask() {
        lifecycle.coroutineScope.launch {
            editAddViewModel.removeTask().collect { uiState ->
                when (uiState) {
                    is UiState.Start -> {}
                    is UiState.Success -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    is UiState.Error -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    }
                }
            }
    }

    private fun addTask() {
        lifecycle.coroutineScope.launch {
            editAddViewModel.addTask().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    is UiState.Error -> {
                        Navigation.findNavController(binding.root).navigate(R.id.nav_home)
                    }
                    else -> {}
                }
            }
        }
    }

    companion object {
        fun newInstance(bundle: Bundle): EditAddFragment {
            val fragment = EditAddFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private fun animation(button : Button) {
        val buttonAnimator = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
        )
        buttonAnimator.duration = 200
        val fadeAnimator = ObjectAnimator.ofFloat(button, View.ALPHA, 1f, 0f)
        fadeAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(buttonAnimator, fadeAnimator)
        val reverseButtonAnimator = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f)
        )
        reverseButtonAnimator.duration = 200
        val reverseFadeAnimator = ObjectAnimator.ofFloat(button, View.ALPHA, 1f)
        reverseFadeAnimator.duration = 200
        val reverseAnimatorSet = AnimatorSet()
        reverseAnimatorSet.playTogether(reverseButtonAnimator, reverseFadeAnimator)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                button.alpha = 1f
                reverseAnimatorSet.start()
            }
        })
        animatorSet.start()
    }
}
