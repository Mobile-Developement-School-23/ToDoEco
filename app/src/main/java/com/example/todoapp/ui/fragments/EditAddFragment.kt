package com.example.todoapp.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.UiState
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.receivers.NotificationReceiver
import com.example.todoapp.ui.util.NotificationHelper
import com.example.todoapp.ui.util.snackbar
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import com.example.todoapp.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

    private fun scheduleNotification(context: Context, notificationTime: Calendar, task : TaskModel) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", task.text)
            when(task.priority) {
                Importance.LOW -> putExtra("priority", "Low importance")
                Importance.BASIC -> putExtra("priority", "Basic importance")
                Importance.IMPORTANT -> putExtra("priority", "High importance")
                else -> putExtra("priority", "Basic importance")
            }
            putExtra("taskId", task.id.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(context, task.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
    }

    private fun createNotification(selectedTime: String, task: TaskModel) {
        if (selectedTime != "99:99") {
            if (task.deadline != null) {
                val timeParts = selectedTime.split(":")
                val hours = timeParts[0].toIntOrNull() ?: 0
                val minutes = timeParts[1].toIntOrNull() ?: 0
                val formattedHours = hours.toString().trimStart('0')
                val formattedMinutes = minutes.toString().trimStart('0')
                val notificationTime = Calendar.getInstance()
                val deadline = task.deadline
                notificationTime.timeInMillis = deadline!!
                val year = notificationTime.get(Calendar.YEAR)
                val month = notificationTime.get(Calendar.MONTH)
                val day = notificationTime.get(Calendar.DAY_OF_MONTH)
                notificationTime.set(Calendar.YEAR, year)
                notificationTime.set(Calendar.MONTH, month)
                notificationTime.set(Calendar.DAY_OF_MONTH, day)
                notificationTime.set(
                    Calendar.HOUR_OF_DAY,
                    if (formattedHours.isEmpty()) 0 else formattedHours.toInt()
                )
                notificationTime.set(
                    Calendar.MINUTE,
                    if (formattedMinutes.isEmpty()) 0 else formattedMinutes.toInt()
                )
                notificationTime.set(Calendar.SECOND, 0)
                scheduleNotification(requireContext(), notificationTime, task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).activityComponent.inject(this)
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
            animation(binding.cancelButton)
            showCancelWarningDialog()
        }
        // обработка нажатия на "Сохранить"
        binding.saveButton.setOnClickListener {
            animation(binding.saveButton)
            when (editAddViewModel.saveOrCreateFlag) {
                1 -> { // сохранить старую заметку
                    fillModel()
                    showSaveWarningDialog()
                }
                2 -> { // создать новую заметку
                    fillModel()
                    showNewSaveWarningDialog()
                }
                else -> Toast.makeText(context,"dgnjkdllxd", Toast.LENGTH_SHORT).show()
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
        binding.notificationButton.setOnClickListener {
            showTimePickerDialog()
        }
        // заполнение данных по объекту из ViewModel
        val text = editAddViewModel.toDoItem.text
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
            calendar.timeInMillis = deadline
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            binding.myDeadlineDatePicker.updateDate(year, month, day)
        } else {
            binding.showCalendar.isChecked = false
            binding.myDeadlineDatePicker.visibility = View.GONE
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
                binding.notificationButton.visibility = View.VISIBLE
                binding.myDeadlineDatePicker.visibility = View.VISIBLE
            } else {
                binding.myDeadlineDatePicker.visibility = View.GONE
                binding.notificationButton.visibility = View.GONE
            }
        }
        binding.notificationButton.setOnClickListener {
            showTimePickerDialog()
        }
        // заполнение данных по объекту из ViewModel
        val text = editAddViewModel.toDoItem.text
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
            calendar.timeInMillis = deadline
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
                val builder = NavOptions.Builder()
                val navOptions: NavOptions =
                    builder.setEnterAnim(R.anim.slide_out_left).setExitAnim(R.anim.slide_in_right)
                        .build()
                Navigation.findNavController(binding.root).navigate(R.id.nav_home, Bundle(), navOptions)
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

    private fun areDatesEqual(date1: Long, date2: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val formattedDate1 = dateFormat.format(Date(date1))
        val formattedDate2 = dateFormat.format(Date(date2))

        return formattedDate1 == formattedDate2
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
        if (deadline == null && editAddViewModel.toDoItem.deadline != null)
            NotificationHelper.deleteNotification(requireContext(), editAddViewModel.toDoItem)

        if (deadline != null && editAddViewModel.selectedTime == "99:99") {
            if (editAddViewModel.toDoItem.deadline == null)
                editAddViewModel.selectedTime = "00:00"
            else {
                when (areDatesEqual(deadline, editAddViewModel.toDoItem.deadline!!)) {
                    true -> {}
                    false -> {
                        editAddViewModel.selectedTime = "00:00"
                    }
                }
            }
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
                    is UiState.Success -> {
                        createNotification(editAddViewModel.selectedTime, editAddViewModel.toDoItem)
                        val builder = NavOptions.Builder()
                        val navOptions: NavOptions =
                            builder.setEnterAnim(R.anim.slide_out_left).setExitAnim(R.anim.slide_in_right)
                                .build()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_home, Bundle(), navOptions)
                    }
                    is UiState.Error -> {view?.snackbar("Error!")}
                    }
                }
            }
    }


    private fun deleteTask() {
        lifecycle.coroutineScope.launch {
            editAddViewModel.removeTask().collect { uiState ->
                when (uiState) {
                    is UiState.Start -> {}
                    is UiState.Success -> {
                        NotificationHelper.deleteNotification(requireContext(),
                            editAddViewModel.toDoItem)
                        val builder = NavOptions.Builder()
                        val navOptions: NavOptions =
                            builder.setEnterAnim(R.anim.slide_out_left).setExitAnim(R.anim.slide_in_right)
                                .build()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_home, Bundle(), navOptions)
                    }
                    is UiState.Error -> view?.snackbar("A deletion error has occurred, try again!")
                    }
                }
            }
    }

    private fun addTask() {
        lifecycle.coroutineScope.launch {
            editAddViewModel.addTask().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        createNotification(editAddViewModel.selectedTime, editAddViewModel.toDoItem)
                        val builder = NavOptions.Builder()
                        val navOptions: NavOptions =
                            builder.setEnterAnim(R.anim.slide_out_left).setExitAnim(R.anim.slide_in_right)
                                .build()
                        Navigation.findNavController(binding.root).navigate(R.id.nav_home, Bundle(), navOptions)
                    }
                    is UiState.Error -> view?.snackbar("There was an error adding a task, try again!")
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
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                editAddViewModel.selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}
