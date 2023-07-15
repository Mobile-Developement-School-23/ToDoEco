package com.example.todoapp.ui.fragments

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.UiState
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.receivers.NotificationReceiver
import com.example.todoapp.ui.themes.Colors
import com.example.todoapp.ui.themes.MainTheme
import com.example.todoapp.ui.util.NotificationHelper
import com.example.todoapp.ui.util.createDateString
import com.example.todoapp.ui.util.dateStringToTimestamp
import com.example.todoapp.ui.util.snackbar
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import com.example.todoapp.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class EditAddFragment : Fragment() {

    @Inject
    lateinit var modelFactory: ViewModelFactory
    private val editAddViewModel: EditAddViewModel by lazy {
        ViewModelProvider(this, modelFactory)[EditAddViewModel::class.java]
    }

    private val text: StateFlow<String> by lazy { editAddViewModel.text }
    private val importance: StateFlow<Importance> by lazy { editAddViewModel.importance }
    private val deadline: StateFlow<Long?> by lazy { editAddViewModel.deadline }
    private val selectedTime: StateFlow<String> by lazy { editAddViewModel.selectedTime }

    private fun scheduleNotification(
        context: Context,
        notificationTime: Calendar,
        task: TaskModel
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", task.text)
            when (task.priority) {
                Importance.LOW -> putExtra("priority", "Low importance")
                Importance.BASIC -> putExtra("priority", "Basic importance")
                Importance.IMPORTANT -> putExtra("priority", "High importance")
                else -> putExtra("priority", "Basic importance")
            }
            putExtra("taskId", task.id.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
    }

    private fun createNotification(task: TaskModel) {
        if (selectedTime.value != "99:99") {
            val hoursMinutes = selectedTime.value
            if (task.deadline != null) {
                val timeParts = hoursMinutes.split(":")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            editAddViewModel.setFlag(it.getInt("SAVE_OR_EDIT_FLAG"))
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    editAddViewModel.setItemById(it.getString("TASK_ID").toString())
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).activityComponent.inject(this)
        return ComposeView(requireContext()).apply {
            setContent {
                MainTheme {
                    MainComponent(
                        close = { findNavController().navigateUp() },
                        editadd = {
                            if (editAddViewModel.saveOrCreateFlag == 2) {
                                lifecycle.coroutineScope.launch {
                                    editAddViewModel.addTask().collect { uiState ->
                                        when (uiState) {
                                            is UiState.Success -> {
                                                if (editAddViewModel.deadline.value != null)
                                                    createNotification(editAddViewModel.toDoItem.value)
                                                findNavController().navigateUp()
                                            }
                                            is UiState.Error -> {}
                                            else -> {}
                                        }
                                    }
                                }
                            } else {
                                lifecycle.coroutineScope.launch {
                                    editAddViewModel.setTask().collect { uiState ->
                                        when (uiState) {
                                            is UiState.Success -> {
                                                if (editAddViewModel.deadline.value != null)
                                                    createNotification(editAddViewModel.toDoItem.value)
                                                findNavController().navigateUp()
                                            }
                                            is UiState.Error -> {}
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        },
                        textFlow = text,
                        textChange = { editAddViewModel.setTaskText(it) },
                        importanceFlow = importance,
                        deadlineFlow = deadline,
                        switchOn = {
                            val dateString = createDateString(Calendar.getInstance())
                            editAddViewModel.setTaskDeadline(dateStringToTimestamp(dateString))
                        },
                        switchOff = {
                            editAddViewModel.setTaskDeadline(null)
                            if (editAddViewModel.toDoItem.value.deadline != null)
                                NotificationHelper.deleteNotification(
                                    requireContext(),
                                    editAddViewModel.toDoItem.value
                                )
                        },
                        deadlineAction = {
                            requireContext().pickDateAndTime { date ->
                                val timestamp = createDateString(date)
                                editAddViewModel.setTaskDeadline(dateStringToTimestamp(timestamp))
                                editAddViewModel.setSelectedTime(
                                    formatDate(
                                        dateStringToTimestamp(
                                            timestamp
                                        )
                                    )
                                )
                                if (editAddViewModel.selectedTime.value == "99:99") {
                                    if (editAddViewModel.toDoItem.value.deadline == null)
                                        editAddViewModel.setSelectedTime("00:00")
                                    else {
                                        when (areDatesEqual(
                                            deadline.value!!,
                                            editAddViewModel.toDoItem.value.deadline!!
                                        )) {
                                            true -> {}
                                            false -> {
                                                editAddViewModel.setSelectedTime("00:00")
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        deleteAction = {
                            lifecycle.coroutineScope.launch {
                                editAddViewModel.removeTask().collect { uiState ->
                                    when (uiState) {
                                        is UiState.Success -> findNavController().navigateUp()
                                        is UiState.Error -> {
                                            this@apply.snackbar(uiState.cause)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainComponentPreview() {
        MainTheme {
            MainComponent(
                close = {  },
                editadd = {
                },
                textFlow = MutableStateFlow("Hello world!"),
                textChange = {  },
                importanceFlow = MutableStateFlow(Importance.LOW),
                deadlineFlow = MutableStateFlow(Date().time),
                switchOn = {
                },
                switchOff = {
                },
                deadlineAction = {},
                deleteAction = {}
            )
        }
    }

    @Composable
    private fun MainComponent(
        close: () -> Unit,
        editadd: () -> Unit,
        textFlow: StateFlow<String>,
        textChange: (String) -> Unit,
        importanceFlow: StateFlow<Importance>,
        deadlineFlow: StateFlow<Long?>,
        switchOn: () -> Unit,
        switchOff: () -> Unit,
        deadlineAction: () -> Unit,
        deleteAction: () -> Unit,
    ) {
        MainTheme {
            Scaffold(
                backgroundColor = MaterialTheme.colors.background,
                topBar = {
                    ToolbarComponent(close, editadd)
                }
            ) { padding ->
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CenteredTextComponent()
                    EditTextComponent(textFlow, textChange)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = MaterialTheme.colors.onBackground)
                    )
                    ImportanceComponent(importanceFlow)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = MaterialTheme.colors.onBackground)
                    )
                    DeadlineComponent(
                        deadlineFlow,
                        switchOn,
                        switchOff,
                        deadlineAction
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = MaterialTheme.colors.onBackground)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = MaterialTheme.colors.onBackground)
                    )
                    DeleteComponent(deleteAction)
                }
            }
        }
    }

    @Preview
    @Composable
    private fun ToolbarComponentPreview() {
        MainTheme() {
            MaterialTheme {
                ToolbarComponent(
                    close = {},
                    editAdd = {}
                )
            }
        }
    }

    @Composable
    fun ToolbarComponent(
        close: () -> Unit,
        editAdd: () -> Unit
    ) {
        val scrollState = rememberScrollState()
        val toolbarElevation by animateDpAsState(
            targetValue = if (scrollState.value > 0) 8.dp else 0.dp,
            animationSpec = tween(durationMillis = 250)
        )

        TopAppBar(
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 0.dp,
            modifier = Modifier.shadow(elevation = toolbarElevation)
        ) {
            Box(Modifier.weight(1f)) {
                IconButton(
                    onClick = close,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.back_svgrepo_com),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
            Button(
                onClick = editAdd,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Colors.sneakySesame,
                    contentColor = MaterialTheme.colors.onPrimary,
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.button,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }

    @Preview
    @Composable
    private fun EditTextComponentPreview() {
        MainTheme() {
            val textState = remember { mutableStateOf("") }
            val textFlow = MutableStateFlow("Hello world!")
            EditTextComponent(
                text = textFlow,
                change = { newText -> textState.value = newText }
            )
        }
    }

    @Composable
    private fun EditTextComponent(
        text: StateFlow<String>,
        change: (String) -> Unit
    ) {
        val value by text.collectAsState()
        var focus by remember { mutableStateOf(false) }
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = MaterialTheme.shapes.large
                    )
                    .onFocusChanged { focus = it.hasFocus },
                value = value,
                onValueChange = change,
                shape = MaterialTheme.shapes.large,
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                label = {
                    Text(
                        text = stringResource(id = R.string.enter_description),
                        style = MaterialTheme.typography.body2.copy(
                            color = when (focus) {
                                true -> MaterialTheme.colors.primary
                                false -> MaterialTheme.colors.onSecondary
                            }
                        ),
                        fontFamily = FontFamily.Monospace
                    )
                }
            )
        }
    }

    @Preview
    @Composable
    private fun ImportanceComponentPreview() {
        MainTheme() {
            val importanceState = remember { mutableStateOf(Importance.BASIC) }
            val importanceFlow = MutableStateFlow(Importance.LOW)

            ImportanceComponent(importance = importanceFlow)
        }
    }

    @Composable
    private fun ImportanceComponent(
        importance: StateFlow<Importance>,
    ) {
        val priority by importance.collectAsState()
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.importance),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = when (priority) {
                        Importance.LOW -> stringResource(id = R.string.low_importance)
                        Importance.BASIC -> stringResource(id = R.string.normal_importance)
                        Importance.IMPORTANT -> stringResource(id = R.string.high_importance)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onSecondary),
                    fontFamily = FontFamily.Monospace
                )
                ToggleGroupWithThreeButtonsComponent(toggleButtonItemsToImportance)
            }
        }
    }

    private val toggleButtonItemsToImportance: (Int) -> Unit = { input: Int ->
        when (input) {
            0 -> editAddViewModel.setTaskImportance(Importance.LOW)
            1 -> editAddViewModel.setTaskImportance(Importance.BASIC)
            2 -> editAddViewModel.setTaskImportance(Importance.IMPORTANT)
        }
    }

    @Preview
    @Composable
    private fun DeadlineComponentPreview() {
        MainTheme() {
            val deadlineState = remember { mutableStateOf<Long?>(null) }
            val deadlineFlow = MutableStateFlow(Date().time)

            DeadlineComponent(
                deadlineFlow = deadlineFlow,
                onSwitchActivation = {},
                onSwitchDeactivation = {},
                onDeadlineClickAction = {}
            )
        }
    }

    @Composable
    private fun DeadlineComponent(
        deadlineFlow: StateFlow<Long?>,
        onSwitchActivation: () -> Unit,
        onSwitchDeactivation: () -> Unit,
        onDeadlineClickAction: () -> Unit
    ) {
        val deadline by deadlineFlow.collectAsState()
        val clickable = (deadline != null)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(clickable) { onDeadlineClickAction() }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.deadline),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary),
                        fontFamily = FontFamily.Monospace
                    )
                }
                Switch(
                    checked = clickable,
                    onCheckedChange = {
                        when (it) {
                            true -> onSwitchActivation()
                            false -> onSwitchDeactivation()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Preview
    @Composable
    private fun DeleteComponentPreview() {
        MainTheme() {
            DeleteComponent(onClick = {})
        }
    }

    @Composable
    private fun DeleteComponent(onClick: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Colors.sneakySesame,
                    contentColor = MaterialTheme.colors.onPrimary,
                    disabledBackgroundColor = Color.Transparent,
                    disabledContentColor = MaterialTheme.colors.onBackground
                ),
                enabled = true,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.delete_3_svgrepo_com),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.remove).uppercase(),
                    style = MaterialTheme.typography.button,
                    fontFamily = FontFamily.Monospace
                )
            }

        }
    }

    @Composable
    fun CenteredTextComponent() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "My task",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }

    @Preview
    @Composable
    private fun ToggleGroupWithThreeButtonsComponentPreview() {
        MainTheme() {
            ToggleGroupWithThreeButtonsComponent(onPositionSelected = {})
        }
    }

    @Preview
    @Composable
    private fun ToggleableButtonComponentPreview() {
        ToggleableButtonComponent(
            icon = ImageVector.vectorResource(id = R.drawable.slow),
            index = 0,
            selectedButtonIndex = 0,
            onButtonSelected = {}
        )
    }

    @Composable
    fun ToggleGroupWithThreeButtonsComponent(onPositionSelected: (Int) -> Unit) {
        val selectedButtonIndex = remember { mutableStateOf(0) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleableButtonComponent(
                icon = ImageVector.vectorResource(id = R.drawable.slow),
                index = 0,
                selectedButtonIndex = selectedButtonIndex.value,
                onButtonSelected = { position ->
                    selectedButtonIndex.value = position
                    onPositionSelected(position)
                }
            )
            ToggleableButtonComponent(
                icon = ImageVector.vectorResource(id = R.drawable.normally),
                index = 1,
                selectedButtonIndex = selectedButtonIndex.value,
                onButtonSelected = { position ->
                    selectedButtonIndex.value = position
                    onPositionSelected(position)
                }
            )
            ToggleableButtonComponent(
                icon = ImageVector.vectorResource(id = R.drawable.urgently),
                index = 2,
                selectedButtonIndex = selectedButtonIndex.value,
                onButtonSelected = { position ->
                    selectedButtonIndex.value = position
                    onPositionSelected(position)
                }
            )
        }
    }

    @Composable
    fun ToggleableButtonComponent(
        icon: ImageVector,
        index: Int,
        selectedButtonIndex: Int,
        onButtonSelected: (Int) -> Unit
    ) {
        val isSelected = index == selectedButtonIndex
        Button(
            onClick = { onButtonSelected(index) },
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

fun Context.pickDateAndTime(timeSet: (Calendar) -> Unit) {
    val currentDateTime = Calendar.getInstance()
    val year1 = currentDateTime.get(Calendar.YEAR)
    val month1 = currentDateTime.get(Calendar.MONTH)
    val day1 = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val hour1 = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val minute1 = currentDateTime.get(Calendar.MINUTE)

    DatePickerDialog(this, R.style.CustomDatePickerStyle, { _, year, month, day ->
        TimePickerDialog(this, R.style.CustomDatePickerStyle, { _, hour, minute ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, day, hour, minute)
            timeSet(pickedDateTime)
        }, hour1, minute1, true).show()
    }, year1, month1, day1).show()
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toDate(): String = run {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm"))
}

fun dateString(day: Int, month: Int, year: Int, hour: Int, minute: Int): String =
    String.format("%02d.%02d.%04d, %02d:%02d", day, month + 1, year, hour, minute)

fun createDateString(calendar: Calendar): String = dateString(
    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE)
)

fun dateStringToTimestamp(dateString: String): Long {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy, hh:mm", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.time ?: 0L
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}

private fun areDatesEqual(date1: Long, date2: Long): Boolean {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val formattedDate1 = dateFormat.format(Date(date1))
    val formattedDate2 = dateFormat.format(Date(date2))
    return formattedDate1 == formattedDate2
}


