package com.example.todoapp.data.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapp.data.db.room.TaskEntity
import com.example.todoapp.data.db.room.ToDoDao

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun taskDao(): ToDoDao
}