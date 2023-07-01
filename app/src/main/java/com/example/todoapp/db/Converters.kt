package com.example.todoapp.db

import androidx.room.TypeConverter
import com.example.todoapp.api.request_response_data.ToDoItemResponse
import java.util.UUID

class Converters {

    @TypeConverter
    fun fromImportance(importance : ToDoItemResponse.Importance) : String {

        return importance.name

    }

    @TypeConverter
    fun toImportance(name : String) : ToDoItemResponse.Importance {

        return ToDoItemResponse.Importance.valueOf(name)

    }


}