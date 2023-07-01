package com.example.todoapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_items")
    fun getAllItems(): Flow<List<ToDoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ToDoItemEntity): Long

    @Query("DELETE FROM todo_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("SELECT * FROM todo_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): ToDoItemEntity?

    @Update
    suspend fun updateItem(item: ToDoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ToDoItemEntity>)

}