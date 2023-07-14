package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.AddTaskUseCase
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import com.example.todoapp.domain.usecases.RemoveTaskUseCase
import com.example.todoapp.domain.usecases.UpdateTaskUseCase
import com.example.todoapp.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class EditAddViewModel(
    private val updateCase: UpdateTaskUseCase,
    private val getAllCase: GetAllTasksUseCase,
    private val getSingleCase: GetItemByIdUseCase,
    private val removeCase: RemoveTaskUseCase,
    private val addCase: AddTaskUseCase
) : ViewModel() {

    private var _saveOrCreateFlag : Int = 0
    val saveOrCreateFlag : Int get() = _saveOrCreateFlag
    private val _selectedTime: MutableStateFlow<String> = MutableStateFlow("99:99")
    val selectedTime: StateFlow<String> = _selectedTime.asStateFlow()

    private val _toDoItem: MutableStateFlow<TaskModel> = MutableStateFlow(TaskModel(UUID.randomUUID()))
    val toDoItem: StateFlow<TaskModel> = _toDoItem.asStateFlow()

    private val _text: MutableStateFlow<String> = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _importance: MutableStateFlow<Importance> = MutableStateFlow(Importance.BASIC)
    val importance: StateFlow<Importance> = _importance.asStateFlow()

    private val _deadline: MutableStateFlow<Long?> = MutableStateFlow(null)
    val deadline: StateFlow<Long?> = _deadline.asStateFlow()

    private val creationDate: MutableStateFlow<Long> = MutableStateFlow(0)

    private val modificationDate: MutableStateFlow<Long?> = MutableStateFlow(0)

    fun addTask(): Flow<UiState<Any>> = flow {
        val taskText: String = text.value
        val taskPriority: Importance = importance.value
        val taskDeadline: Long? = deadline.value
        emit(UiState.Start)
        addCase(taskText, taskPriority, taskDeadline)
        emit(UiState.Success("Task add!"))
    }.catch {
        emit(UiState.Error(it.cause?.message.toString()))
    }

    fun setTask(): Flow<UiState<String>> = flow {
        val task = toDoItem.value
        task.text = _text.value
        task.priority = _importance.value
        task.deadline = _deadline.value
        task.modifyingTime = Date().time
        emit(UiState.Start)
        updateCase(task)
        emit(UiState.Success("Task modified!"))
    }.catch {
        emit(UiState.Error(it.stackTraceToString()))
    }

    suspend fun removeTask(): Flow<UiState<String>> = flow {
        val task = toDoItem.value
        emit(UiState.Start)
        removeCase(task)
        emit(UiState.Success("Task deleted!"))
    }.catch {
        emit(UiState.Error(it.stackTraceToString()))
    }

    private fun requireTask(id: UUID): Flow<UiState<TaskModel>> = flow {
        getSingleCase(id).collect { dataState ->
            when (dataState) {
                is DataState.Result -> emit(UiState.Success(dataState.data))
                is DataState.Exception -> emit(UiState.Error(dataState.cause.message ?: ""))
                else -> emit(UiState.Start)
            }
        }
    }

    private suspend fun setTaskByParemeters(task: TaskModel) {
        _toDoItem.emit(task)
        _text.emit(task.text)
        _importance.emit(task.priority)
        _deadline.emit(task.deadline)
        creationDate.emit(task.creationTime)
        modificationDate.emit(task.modifyingTime)
    }

    fun setFlag(flag : Int) {
        this._saveOrCreateFlag = flag
    }

    fun setSelectedTime(selectedTime: String) {
        viewModelScope.launch {
            _selectedTime.emit(selectedTime)
        }
    }

    fun setTaskText(text: String) {
        viewModelScope.launch {
            _text.emit(text)
        }
    }

    fun setTaskImportance(priority: Importance) {
        viewModelScope.launch {
            _importance.emit(priority)
        }
    }

    fun setTaskDeadline(deadline: Long?) {
        viewModelScope.launch {
            _deadline.emit(deadline)
        }
    }

    suspend fun setItemById(id: String) {
        if (id != "-1") {
            requireTask(UUID.fromString(id)).collect { dataState ->
                when (dataState) {
                    is UiState.Success -> {
                        setTaskByParemeters(dataState.data)
                    }
                    is UiState.Error -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }
}