package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.data.ToDoListener
import com.example.todoapp.data.ToDoRepository
import java.util.Calendar

class HomeViewModel(
    private val toDoRepo: ToDoRepository // коррект
) : ViewModel() {

    private val _todoList: MutableLiveData<List<ToDoItem>> = MutableLiveData() // коррект
    val toDoList: LiveData<List<ToDoItem>> get() = _todoList // коррект

    private var _counterToDo = MutableLiveData<Int>()  // коррект
    val counterToDo : LiveData<Int> get() = _counterToDo  // коррект

    private val listener: ToDoListener = { // коррект

        _todoList.value = it

    }

    init {

        loadTasks()  // коррект

        _counterToDo.value = toDoList.value?.count { it.isDone }!!

    }

    override fun onCleared() {  // коррект

        super.onCleared()  // коррект
        toDoRepo.removeListener(listener)  // коррект

    }

    fun loadTasks() { // можно видеть список позиций

        toDoRepo.addListener(listener)

    }


    fun incrementCounterToDo() { // дело сделано

        _counterToDo.value = _counterToDo.value!! + 1

    }

    fun decrementCounterToDo() { // дело не сделано

        _counterToDo.value = _counterToDo.value!! - 1

    }


    fun removeDataFromRepo(item: ToDoItem) { //удаление через Попап и свайпы

        if (item.isDone)
            _counterToDo.value = _counterToDo.value!! - 1

        toDoRepo.removeItem(item)

    }


    fun setCheckStatusToRepo(item: ToDoItem, isChecked : Boolean) { // поставить сделанное дело

        toDoRepo.setCheckStatus(item, isChecked)

    }

    fun getItemById(id : String) = toDoRepo.getToDoItemById(id)

    fun swapElementsToRepository(item1: ToDoItem, item2: ToDoItem) {

        toDoRepo.swapElements(item1, item2)

    }

    fun backToTheRepository(item: ToDoItem) {

        toDoRepo.itemBack(item)

    }

}