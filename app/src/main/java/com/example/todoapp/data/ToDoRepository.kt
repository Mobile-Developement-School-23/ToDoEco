package com.example.todoapp.data
import android.util.Log
import com.example.todoapp.exceptions.TaskNotFoundException
import java.util.ArrayList
import java.util.Collections
import java.util.Date

typealias ToDoListener = (users: List<ToDoItem>) -> Unit

class ToDoRepository {

    private var todoList: MutableList<ToDoItem> = mutableListOf() // коррект

    private val listeners = mutableSetOf<ToDoListener>()

    private var _id: String = "1" // коррект
    val id : String get() = _id // коррект

    init { // коррект

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

    fun getToDoList(): List<ToDoItem> { // коррект

        return todoList // коррект

    }


    fun addItem(toDoItem: ToDoItem) { // коррект

        // добавление нового элемента полностью

        toDoItem.id = this.id

        todoList = ArrayList(todoList)
        todoList.add(toDoItem) // коррект

        val number = _id.toInt()
        val incrementedNumber = number + 1
        this._id = incrementedNumber.toString()

    } // коррект

    fun removeItem(toDoItem: ToDoItem) { // коррект

        val indexToDelete = todoList.indexOfFirst { it.id == toDoItem.id }

        if (indexToDelete != -1) {

            todoList = ArrayList(todoList)
            todoList.removeAt(indexToDelete)
            notifyChanges()

        }

    }

    fun saveData(toDoItem: ToDoItem) {

        val index = todoList.indexOfFirst { it.id == toDoItem.id }

        if (index != -1) {

            todoList = ArrayList(todoList)
            todoList[index] = toDoItem
            notifyChanges()

        }

    }

    fun setCheckStatus(item: ToDoItem, isChecked: Boolean) {

        val index = todoList.indexOfFirst { it.id == item.id }

        if (index != -1) {

            todoList = ArrayList(todoList)
            todoList[index].isDone = isChecked
            notifyChanges()

        }

        notifyChanges()

    }

    fun addListener(listener: ToDoListener) {

        listeners.add(listener)
        listener.invoke(todoList)

    }

    fun removeListener(listener: ToDoListener) {

        listeners.remove(listener)

    }

    private fun notifyChanges() {

        listeners.forEach { it.invoke(todoList) }

    }

     fun getToDoItemById(id : String) : ToDoItem {

        val index = todoList.indexOfFirst { it.id == id }

         val toDoItem : ToDoItem
         if (index != -1) {

             toDoItem = todoList[index]
             return toDoItem

         }
         else
             throw TaskNotFoundException()

    }

    fun swapElements(item1: ToDoItem, item2: ToDoItem) {

        val index1 = todoList.indexOfFirst { it.id == item1.id }
        val index2 = todoList.indexOfFirst { it.id == item2.id }

        todoList = ArrayList(todoList)
        todoList[index1] = item2
        todoList[index2] = item1

        notifyChanges()

    }

    fun itemBack(item: ToDoItem) {

        todoList = ArrayList(todoList)
        todoList.add(0, item)

        notifyChanges()

    }


}

