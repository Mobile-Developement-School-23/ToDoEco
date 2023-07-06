package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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


    private var _toDoItem : TaskModel = TaskModel(UUID.randomUUID(),
        "", Importance.BASIC, false, Date().time, null, Date().time)
    val toDoItem : TaskModel get() = _toDoItem



    fun addTask(): Flow<UiState<String>> = flow {
        emit(UiState.Start)
        addCase(_toDoItem.text, _toDoItem.priority, _toDoItem.deadline)
        emit(UiState.Success("Task added!"))
    }.catch {

        emit(UiState.Error(it.message ?: "Unrecognized exception!"))
    }

    fun setTask(): Flow<UiState<String>> = flow {
        val task = _toDoItem
        emit(UiState.Start)
        updateCase(task)
        emit(UiState.Success("Task modified!"))
    }.catch {
        emit(UiState.Error(it.stackTraceToString()))
    }

    suspend fun removeTask(): Flow<UiState<String>> = flow {
        val task = _toDoItem
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

    fun setFlag(flag : Int) {
        this._saveOrCreateFlag = flag
    }


    suspend fun setItemById(id: String) {
        if (id != "-1") {
            requireTask(UUID.fromString(id)).collect { dataState ->
                when (dataState) {
                    is UiState.Success -> {
                        _toDoItem = dataState.data
                    }

                    is UiState.Error -> {
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun setItemByObject(item: TaskModel) {

        this._toDoItem = item

    }

}