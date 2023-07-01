package com.example.todoapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.todoapp.api.request_response_data.ToDoItemResponse
import com.example.todoapp.data.ToDoRepository
import com.example.todoapp.db.ToDoItemEntity
import java.util.Calendar

class EditAddViewModel(

    private val toDoRepository: ToDoRepository

) : ViewModel() {


    private var _saveOrCreateFlag : Int = 0
    val saveOrCreateFlag : Int get() = _saveOrCreateFlag


    var _toDoItem : ToDoItemEntity = ToDoItemEntity("0", "", ToDoItemResponse.Importance.basic, null,
        false, "color", 0, null)
    val toDoItem : ToDoItemEntity get() = _toDoItem



    suspend fun deleteTask() {

        toDoRepository.deleteFromInternet(toDoItem.id)

    }

    suspend fun saveTask() {

        toDoRepository.changeOnInternet(toDoItem.id, toDoItem)

    }

    suspend fun addTask() {

        toDoRepository.addToInternet(toDoItem)

    }


    fun setFlag(flag : Int) {

        this._saveOrCreateFlag = flag

    }


    suspend fun setItemById(id: String) {

        if (id == "-1")
            _toDoItem =  ToDoItemEntity("0", "", ToDoItemResponse.Importance.basic, null,
                false, "color", 0, null)
        else
            _toDoItem = toDoRepository.getItemById(id)!!

    }

    fun setItemByObject(item: ToDoItemEntity) {

        this._toDoItem = item

    }

}