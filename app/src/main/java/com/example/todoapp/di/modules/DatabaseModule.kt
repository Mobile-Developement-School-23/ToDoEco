package com.example.todoapp.di.modules

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.db.room.ToDoDatabase
import com.example.todoapp.data.db.DatabaseRepository
import com.example.todoapp.data.db.room.ToDoDao
import com.example.todoapp.data.util.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): ToDoDatabase = Room
        .databaseBuilder(
            context,
            ToDoDatabase::class.java,
            "task_database"
        ).build()

    @Provides
    @Singleton
    fun provideTaskDao(database: ToDoDatabase): ToDoDao = database.taskDao()

}