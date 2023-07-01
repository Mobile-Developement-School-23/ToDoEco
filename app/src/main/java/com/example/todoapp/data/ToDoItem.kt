package com.example.todoapp.data

import com.example.todoapp.api.request_response_data.ToDoItemResponse
import java.util.Date

data class ToDoItem(

    val id: String,
    var text: String,
    var importance: ToDoItemResponse.Importance,
    var deadline: Date? = null,
    var isDone: Boolean,
    val creationDate: Date,
    var modificationDate: Date? = null

) {

}