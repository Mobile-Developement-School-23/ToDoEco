package com.example.todoapp.domain

import android.util.Log
import com.example.todoapp.data.db.DatabaseRepository
import com.example.todoapp.data.db.RoomState
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.data.network.NetworkState
import com.example.todoapp.data.util.SharedPreferenceHelper
import com.example.todoapp.data.util.toEntity
import com.example.todoapp.domain.exceptions.ValidationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.util.UUID

class TaskRepository(
    private val databaseSource: DatabaseRepository,
    private val networkSource: NetworkRepository,
    private val preferenceHelper: SharedPreferenceHelper
) {

    fun getAllTasks(): Flow<DataState<List<TaskModel>>> = flow {
        databaseSource.getTasks().collect {
            when (it) {
                is RoomState.Initial -> DataState.Initial
                is RoomState.Success -> emit(DataState.Result(it.data))
                is RoomState.Failure -> emit(DataState.Exception(it.err))
            }
        }
    }

    suspend fun mergeTasks() {
        Log.d("ОШЩЩШ", "jxl")
        networkSource.getTasks().collect { listNetworkState ->
            when (listNetworkState) {
                is NetworkState.Loading -> {}
                is NetworkState.Failure -> {
                    Log.d("ОШЩЩШ", listNetworkState.cause.message.toString())
                }
                is NetworkState.Success -> {
                    Log.d("САКСЭЭЭС", "лдтл")
                    val oldRevision: Int = preferenceHelper.getIntValue()
                    val oldDataList: List<TaskModel> = databaseSource.getTasksAsList()
                    val isActual: Boolean = oldRevision >= listNetworkState.revision
                    Log.d("ОШЩЩШ", isActual.toString())
                    val data: List<TaskModel> = listOf(
                        oldDataList.map { Pair(true, it) },
                        listNetworkState.data.map { Pair(false, it) }
                    ).flatten().groupBy { it.second.id }.map {
                        when (it.value.size) {
                            1 -> when (it.value.first().first == isActual) {
                                true -> it.value.first().second
                                else -> null
                            }

                            else -> it.value.maxByOrNull { pair ->
                                maxOf(
                                    pair.second.creationTime,
                                    pair.second.modifyingTime!!
                                )
                            }?.second
                        }
                    }.filterNotNull()
                    networkSource.patchTasks(data).collect { state ->
                        when (state) {
                            is NetworkState.Loading -> {}
                            is NetworkState.Failure -> throw Exception("Failure!")
                            is NetworkState.Success -> {
                                databaseSource.overwriteDatabase(state.data)
                                preferenceHelper.setIntValue(state.revision)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getTaskById(id: UUID): Flow<DataState<TaskModel>> = flow {
        emit(DataState.Initial)
        databaseSource.getTask(id).collect { roomState ->
            when (roomState) {
                is RoomState.Success -> emit(DataState.Result(roomState.data))
                is RoomState.Failure -> emit(DataState.Exception(roomState.err))
                else -> {}
            }
        }
    }.catch {
        emit(DataState.Exception(it))
    }

    @Throws(ValidationException::class)
    suspend fun addTask(
        text: String, priority: Importance, deadline: Long?
    ) {
        validateTask(text, deadline)
        val task = buildTaskModel(text, priority, deadline)
        databaseSource.updateTask(buildTaskModel(text, priority, deadline).toEntity())
        networkSource.postTask(task).collect { networkState ->
            when (networkState) {
                is NetworkState.Failure -> preferenceHelper.updateRevision()
                else -> {}
            }
        }
    }

    suspend fun removeTask(task: TaskModel) {
        databaseSource.removeTask(task.toEntity())
        networkSource.deleteTask(task).collect { networkState ->
            when (networkState) {
                is NetworkState.Failure -> preferenceHelper.updateRevision()
                else -> {}
            }
        }
    }

    @Throws(ValidationException::class)
    suspend fun updateTask(task: TaskModel) {
        validateTask(task.text, task.deadline)
        databaseSource.updateTask(task.copy(modifyingTime = System.currentTimeMillis()).toEntity())
        networkSource.putTask(task).collect { networkState ->
            when (networkState) {
                is NetworkState.Failure -> preferenceHelper.updateRevision()
                else -> {}
            }
        }
    }

    @Throws(ValidationException::class)
    private fun validateTask(text: String, deadline: Long?) {
        if (text.isBlank()) {
            throw ValidationException("Text is blank!")
        }
        if ((deadline ?: 0) < 0) {
            throw ValidationException("Deadline: $deadline is not valid!")
        }
    }

    private fun getUUID(): UUID = UUID.randomUUID()

    private fun buildTaskModel(
        text: String, priority: Importance, deadline: Long?
    ): TaskModel = TaskModel(
        getUUID(), text, priority, false, System.currentTimeMillis(), deadline, null
    )
}
