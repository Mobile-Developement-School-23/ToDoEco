package com.example.todoapp.data

import java.util.Date

data class ToDoItem(
    var id: String,
    var text: String,
    var importance: Importance,
    var deadline: Date? = null,
    var isDone: Boolean,
    val creationDate: Date,
    var modificationDate: Date? = null
) {
    enum class Importance {
        LOW, NORMAL, URGENT
    }
}
