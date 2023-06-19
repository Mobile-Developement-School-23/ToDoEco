package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.data.ToDoRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeAndAddViewModel() : ViewModel() {

    private val _todoList: MutableLiveData<List<ToDoItem>> = MutableLiveData()
    val toDoList: MutableLiveData<List<ToDoItem>> get() = _todoList
    private val toDoRepo = ToDoRepository()
    private var filledModel: ToDoItem = ToDoItem(toDoRepo.id, "", ToDoItem.Importance.NORMAL, null,
        false, Calendar.getInstance().time, null)
    private var stateFlag = 0
    private var _counterToDo = MutableLiveData<Int>()
    val counterToDo : LiveData<Int> get() = _counterToDo
    private var _positionToInfo = 0
    val positionToInfo : Int get() = _positionToInfo

    init {

        fetchToDOList()
        _counterToDo.value = toDoList.value?.count { it.isDone }!!



    }

    fun fetchToDOList() {

        toDoList.value = toDoRepo.getToDoListFlow()

    }

    fun incrementCounterToDo() {

        _counterToDo.value = _counterToDo.value!! + 1

    }

    fun decrementCounterToDo() {

        _counterToDo.value = _counterToDo.value!! - 1

    }

    fun setFilledModel(item: ToDoItem) {

        filledModel = item

    }

    fun getFilledModel(): ToDoItem {

        return filledModel

    }

    fun removeFilledModel() {
        val filledModel: ToDoItem = ToDoItem(toDoRepo.id, "", ToDoItem.Importance.NORMAL, null,
            false, Calendar.getInstance().time, null)
        this.filledModel = filledModel
    }

    fun setStateFlag(value: Int) {
        stateFlag = value
    }

    fun getStateFlag(): Int {
        return stateFlag
    }

    fun saveDataToRepo() {

        toDoRepo.saveDataItemById(filledModel.id, filledModel)

    }

    fun removeDataFromRepo() {

        if (filledModel.isDone)
            _counterToDo.value = _counterToDo.value!! - 1

        toDoRepo.removeItemById(filledModel.id)

    }

    fun addDataToRepo() {

        toDoRepo.addItem(filledModel)

    }

    fun nextId() {

        toDoRepo.nextId()

    }

    fun setCheckStatusToRepo(position: Int, isChecked: Boolean) {

        toDoRepo.setCheckStatus(position, isChecked)

    }

    fun removeDataFromRepoByPosition(position: Int) {

        if (filledModel.isDone)
            _counterToDo.value = _counterToDo.value!! - 1

        toDoRepo.removeItemByPosition(position)

    }

    fun backToTheRepo() {

        if (filledModel.isDone)
            _counterToDo.value = _counterToDo.value!! + 1
        toDoRepo.back(filledModel)

    }

    fun swapElementsToRepository(position_from: Int, position_to: Int) {

        toDoRepo.swapElements(position_from, position_to)

    }
}