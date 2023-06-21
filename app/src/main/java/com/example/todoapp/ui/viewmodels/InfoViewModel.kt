package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.ToDoRepository

class InfoViewModel(

    private val toDoRepository: ToDoRepository

) : ViewModel() {

    fun getItemById(id : String) = toDoRepository.getToDoItemById(id)

}
