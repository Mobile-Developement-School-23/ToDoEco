package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.network.network_util.ConnectivityObserver
import com.example.todoapp.data.network.network_util.NetworkConnectivityObserver
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.AddTaskUseCase
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import com.example.todoapp.domain.usecases.RemoveTaskUseCase
import com.example.todoapp.domain.usecases.UpdateTaskUseCase
import com.example.todoapp.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(
    private val updateCase: UpdateTaskUseCase,
    private val getAllCase: GetAllTasksUseCase,
    private val getSingleCase: GetItemByIdUseCase,
    private val removeCase: RemoveTaskUseCase,
    private val addCase: AddTaskUseCase,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel()  {

    private var job: Job? = null
    private val _tasks: Flow<DataState<List<TaskModel>>> = getAllCase()

    private val _status = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val status = _status.asStateFlow()

    private val _visibility: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val visibility: StateFlow<Boolean> get() = _visibility

    private val _allTasks: MutableStateFlow<UiState<List<TaskModel>>> = MutableStateFlow(UiState.Start)
    val allTasks: StateFlow<UiState<List<TaskModel>>> get() = _allTasks

    private val _undoneTasks: MutableStateFlow<UiState<List<TaskModel>>> = MutableStateFlow(UiState.Start)
    val undoneTasks: StateFlow<UiState<List<TaskModel>>> get() = _undoneTasks

    private val _doneCounter: MutableStateFlow<Int> = MutableStateFlow(0)
    val doneCounter: StateFlow<Int> get() = _doneCounter

    init {
        startObserveNetwork()
        job = viewModelScope.launch(Dispatchers.IO) {
            _tasks.collect { state ->
                when (state) {
                    is DataState.Result -> {
                        _allTasks.emit(UiState.Success(state.data))
                        _undoneTasks.emit(UiState.Success(state.data.filter { !it.isDone }))
                        _doneCounter.value = state.data.count { it.isDone }
                    }
                    is DataState.Exception -> {
                        _allTasks.emit(UiState.Error(state.cause.message ?: ""))
                        _undoneTasks.emit(UiState.Error(state.cause.message ?: ""))
                    }
                    else -> {
                        _allTasks.emit(UiState.Start)
                        _undoneTasks.emit(UiState.Start)
                    }
                }
            }
        }
    }

    private fun startObserveNetwork() {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest {
                _status.emit(it)
            }
        }
    }

    fun addTask(text: String, priority: Importance, deadline: Long?): Flow<UiState<String>> = flow {
        emit(UiState.Start)
        addCase(text, priority, deadline)
        emit(UiState.Success("Task added!"))
    }.catch {
        emit(UiState.Error(it.message ?: "Unrecognized exception!"))
    }

    suspend fun setTask(task: TaskModel): Flow<UiState<String>> = flow {
        emit(UiState.Start)
        updateCase(task)
        emit(UiState.Success("Task modified!"))
    }.catch {
        emit(UiState.Error(it.stackTraceToString()))
    }

    suspend fun removeTask(task: TaskModel): Flow<UiState<String>> = flow {
        emit(UiState.Start)
        removeCase(task)
        emit(UiState.Success("Task deleted!"))
    }.catch {
        emit(UiState.Error(it.stackTraceToString()))
    }

    fun requireTask(id: UUID): Flow<UiState<TaskModel>> = flow {
        getSingleCase(id).collect { dataState ->
            when (dataState) {
                is DataState.Result -> emit(UiState.Success(dataState.data))
                is DataState.Exception -> emit(UiState.Error(dataState.cause.message ?: ""))
                else -> emit(UiState.Start)
            }
        }
    }

    fun invertVisibilityState() {
        _visibility.value = !_visibility.value
    }

    override fun onCleared() {
        job?.cancel()
    }

    fun incrementDoneCounter() {
        _doneCounter.value += 1
    }

    fun decrementDoneCounter() {
        _doneCounter.value -= 1
    }

}