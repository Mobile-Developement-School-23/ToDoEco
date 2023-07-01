package com.example.todoapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ToDoItemEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun getToDoDao() : ToDoDao

    companion object {

        private var instance : ToDoDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {

            instance ?: createDatabase(context).also { instance = it }

        }

        private fun createDatabase(context: Context) =

            Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java,
                "to_do_db.db"
            ).build()

    }

}