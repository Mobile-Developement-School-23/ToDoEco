package com.example.todoapp.data.network

import com.example.todoapp.data.network.request_response_data.request.TaskListRequest
import com.example.todoapp.data.network.request_response_data.request.TaskRequest
import com.example.todoapp.data.network.request_response_data.responce.Task
import com.example.todoapp.data.util.SharedPreferenceHelper
import com.example.todoapp.data.util.toDto
import com.example.todoapp.data.util.toModel
import com.example.todoapp.domain.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class NetworkRepository(
    private val preferenceHelper: SharedPreferenceHelper,
    private val taskApi: ToDoAPI
) {
    fun getTasks(): Flow<NetworkState<List<TaskModel>>> = flow {
        emit(NetworkState.Loading)
        val response = taskApi.getTasks()
        emit(NetworkState.Success(response.list.map(Task::toModel), response.revision))
    }.catch {
        NetworkState.Failure(it)
    }

    fun patchTasks(list: List<TaskModel>): Flow<NetworkState<List<TaskModel>>> = flow {
        emit(NetworkState.Loading)
        val revision = preferenceHelper.getIntValue()
        val response = taskApi.patchTasks(
            revision,
            TaskListRequest(list.map(TaskModel::toDto))
        )
        preferenceHelper.setIntValue(response.revision)
        emit(NetworkState.Success(response.list.map(Task::toModel), response.revision))
    }.catch {
        emit(NetworkState.Failure(it))
    }

    fun postTask(task: TaskModel): Flow<NetworkState<TaskModel>> = flow {
        emit(NetworkState.Loading)
        val revision = preferenceHelper.getIntValue()
        val response = taskApi.postTask(revision, TaskRequest(task.toDto()))
        preferenceHelper.setIntValue(response.revision)
        emit(NetworkState.Success(response.element.toModel(), response.revision))
    }.catch {
        emit(NetworkState.Failure(it))
    }

    fun putTask(task: TaskModel): Flow<NetworkState<TaskModel>> = flow {
        emit(NetworkState.Loading)
        val revision = preferenceHelper.getIntValue()
        val response = taskApi.putTask(revision, task.id, TaskRequest(task.toDto()))
        preferenceHelper.setIntValue(response.revision)
        emit(NetworkState.Success(response.element.toModel(), response.revision))
    }.catch {
        emit(NetworkState.Failure(it))
    }

    fun deleteTask(task: TaskModel): Flow<NetworkState<TaskModel>> = flow {
        emit(NetworkState.Loading)
        val revision = preferenceHelper.getIntValue()
        val response = taskApi.deleteTask(revision, task.id)
        preferenceHelper.setIntValue(response.revision)
        emit(NetworkState.Success(response.element.toModel(), revision))
    }    .catch {
        emit(NetworkState.Failure(it))
    }
}