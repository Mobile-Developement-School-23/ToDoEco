package com.example.todoapp.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections
import java.util.Date

class ToDoRepository {

    private var todoList: MutableStateFlow<MutableList<ToDoItem>> = MutableStateFlow(emptyList<ToDoItem>().toMutableList())

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

    fun getToDoListFlow(): Flow<List<ToDoItem>> {

        return todoList

    }

    fun addItem(task: ToDoItem) {

        todoList.value.add(task)

    }

    fun removeItemById(id : String) {


    }

    fun saveDataItemById(id: String, filledModel: ToDoItem) {

        todoList.update { list ->
            val updatedList = list.toMutableList()
            val index = list.indexOfFirst { it.id == filledModel.id }
            if (index != -1)
                updatedList[index] = filledModel
            updatedList
        }

    }

    fun setCheckStatus(position: Int, isChecked: Boolean) {

        todoList.value[position].isDone = isChecked

    }

    fun removeItemByPosition(position: Int) {

        todoList.value.removeAt(position)

    }

    fun back(position: Int, filledModel: ToDoItem) {

        todoList.value.add(position, filledModel)

    }

    fun swapElements(position_from: Int, position_to: Int) {

        Collections.swap(todoList.value, position_from, position_to)

    }

}