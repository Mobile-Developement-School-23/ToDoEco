package com.example.todoapp.data.db

import com.example.todoapp.data.db.room.TaskEntity
import com.example.todoapp.data.db.room.ToDoDao
import com.example.todoapp.data.util.SharedPreferenceHelper
import com.example.todoapp.data.util.toEntity
import com.example.todoapp.data.util.toModel
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.exceptions.ValidationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.UUID

class DatabaseRepository(

    private val committer: SharedPreferenceHelper,
    private val dao: ToDoDao
) {
    fun getTask(id: UUID): Flow<RoomState<TaskModel>> = flow {
        emit(RoomState.Initial)
        dao.getTask(id.toString()).collect {
            when (it) {
                null -> emit(RoomState.Failure(ValidationException("Item not found!")))
                else -> emit(RoomState.Success(it.toModel(), committer.getIntValue()))
            }
        }
    }.catch {
        emit(RoomState.Failure(it))
    }

    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)

    suspend fun removeTask(task: TaskEntity) = dao.removeTask(task)

    fun getTasks(): Flow<RoomState<List<TaskModel>>> = flow {
        emit(RoomState.Initial)
        dao.getTasks().collect { list ->
            list.map(TaskEntity::toModel)
                .sortedByDescending { maxOf(it.creationTime, it.modifyingTime ?: 0) }
                .also { tasks -> emit(RoomState.Success(tasks, committer.getIntValue())) }
        }
    }.catch {
        emit(RoomState.Failure(it))
    }

    fun getTasksAsList(): List<TaskModel> = dao.getOrdinaryList().map(TaskEntity::toModel)

    fun overwriteDatabase(list: List<TaskModel>) {
        dao.removeTasks()
        dao.updateTasks(list.map(TaskModel::toEntity))
    }

    suspend fun upsertTasks(list: List<TaskModel>, revision: Int) {
        dao.removeTasks()
        dao.updateTasks(list.map(TaskModel::toEntity))
        committer.setIntValue(revision)
    }
}