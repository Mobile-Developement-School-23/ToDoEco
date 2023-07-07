package com.example.todoapp.data.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todoapp.data.db.room.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: String): Flow<TaskEntity?>
    @Insert(entity = TaskEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: TaskEntity)
    @Delete(entity = TaskEntity::class)
    suspend fun removeTask(task: TaskEntity)
    @Query("SELECT * FROM tasks")
    fun getTasks(): Flow<List<TaskEntity>>
    @Insert(entity = TaskEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateTasks(tasks: List<TaskEntity>)
    @Query("DELETE FROM tasks")
    fun removeTasks()
    @Query("SELECT * FROM tasks")
    fun getOrdinaryList(): List<TaskEntity>
}