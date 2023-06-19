package com.example.todoapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections
import java.util.Date
import kotlin.properties.Delegates

class ToDoRepository {

    private var todoList: MutableList<ToDoItem> = mutableListOf()
    private var _id: String = "11"
    val id : String get() = _id

    init {

        addItem(ToDoItem("1", "Complete project report", ToDoItem.Importance.NORMAL, Date(), false, Date(), null))
        addItem(ToDoItem("2", "Buy groceries", ToDoItem.Importance.LOW, null, false, Date(), null))
        addItem(ToDoItem("3", "Call client", ToDoItem.Importance.URGENT, null, false, Date(), null))
        addItem(ToDoItem("4", "Pay bills", ToDoItem.Importance.NORMAL, Date(), true, Date(), Date()))
        addItem(ToDoItem("5", "Schedule appointment", ToDoItem.Importance.LOW, null, false, Date(), null))
        addItem(ToDoItem("6", "Submit expense report", ToDoItem.Importance.URGENT, Date(), false, Date(), null))
        addItem(ToDoItem("7", "Read book", ToDoItem.Importance.LOW, null, false, Date(), null))
        addItem(ToDoItem("8", "Attend team meeting", ToDoItem.Importance.NORMAL, Date(), true, Date(), Date()))
        addItem(ToDoItem("9", "Clean the house", ToDoItem.Importance.LOW, null, false, Date(), null))
        addItem(ToDoItem("10", "Exercise", ToDoItem.Importance.NORMAL, Date(), false, Date(), null))

    }

    fun getToDoListFlow(): List<ToDoItem> {

        return todoList

    }


    fun addItem(task: ToDoItem) {

        todoList.add(task)

    }

    fun removeItemById(id : String) {

        todoList.removeIf { item -> item.id == id }

    }

    fun saveDataItemById(id: String, filledModel: ToDoItem) {

        val index = todoList.indexOfFirst { it.id == id }

        if (index != -1) {

            todoList[index] = filledModel

        }

    }

    fun setCheckStatus(position: Int, isChecked: Boolean) {

        todoList[position].isDone = isChecked

    }

    fun removeItemByPosition(position: Int) {

        todoList.removeAt(position)

    }

    fun back(filledModel: ToDoItem) {

        todoList.add(0, filledModel)

    }

    fun swapElements(position_from: Int, position_to: Int) {

        Collections.swap(todoList, position_from, position_to)

    }

    fun nextId() {

        val number = _id.toInt()
        val incrementedNumber = number + 1
        this._id = incrementedNumber.toString()

    }

}

