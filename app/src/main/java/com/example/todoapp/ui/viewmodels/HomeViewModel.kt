package com.example.todoapp.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.ToDoApplication
import com.example.todoapp.api.RetrofitInstance
import com.example.todoapp.db.ToDoItemEntity
import com.example.todoapp.api.request_response_data.TaskListResponse
import com.example.todoapp.data.ToDoItem
import com.example.todoapp.data.ToDoRepository
import com.example.todoapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.UUID

class HomeViewModel(

    app: Application,
    private val toDoRepo: ToDoRepository

) : AndroidViewModel(app) {

    private val _todoList: MutableStateFlow<Resource<List<ToDoItemEntity>>> = MutableStateFlow(Resource.Loading())
    val toDoList: StateFlow<Resource<List<ToDoItemEntity>>> = _todoList

    private val _counterToDo: MutableStateFlow<Int> = MutableStateFlow(0)
    val counterToDo: StateFlow<Int> = _counterToDo
//
//    private val _isInternetConnected = MutableLiveData<Boolean>()
//    val isInternetConnected: LiveData<Boolean>
//        get() = _isInternetConnected


    init {

//        _isInternetConnected.value = false
        getToDo()

    }

//    fun setInternetConnected(connected: Boolean) {
//
//        _isInternetConnected.postValue(connected)
//
//    }

    fun setCounter(count : Int) {

        _counterToDo.value = count

    }

    private fun getToDo() = viewModelScope.launch(Dispatchers.IO) {

        _todoList.emit(Resource.Loading())

        try {

            if ( hasInternetConnection() ) {

                toDoRepo.getToDoItems()

                    .onStart { _todoList.emit( Resource.Loading() ) }
                    .catch { e -> _todoList.emit( Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}") ) }
                    .collect { result ->
                        _todoList.emit(Resource.Success(result.data!!))
                    }

            } else {

                _todoList.emit( Resource.Error("No Internet Connection") )

            }

        } catch (t: Throwable) {

            when (t) {

                is IOException -> _todoList.emit( Resource.Error("Network Failure") )
                is HttpException -> _todoList.emit( Resource.Error("Request failed with ${t.code()}: ${t.message()}") )
                else -> _todoList.emit( Resource.Error("Conversion Error") )

            }

        }

    }

    fun hasInternetConnection(): Boolean {

        val connectivityManager = getApplication<ToDoApplication>().getSystemService(

            Context.CONNECTIVITY_SERVICE

        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

    }

    fun deleteTask(id: String, isCheck : Boolean) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepo.deleteFromInternet(id)

                if (response is Resource.Success) {

                    if (isCheck)
                        decrementCounterToDo()
                    deleteTaskFromLocalDb(id)

                } else if (response is Resource.Error) {

                    handleDeleteTaskError(response.message)

                }

            } else {

                if (isCheck)
                    decrementCounterToDo()

                deleteTaskFromLocalDb(id)
                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }

    fun updateTasks() {

        getToDo()

    }

    fun addTask(item: ToDoItemEntity) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepo.addToInternet(item)

                if (response is Resource.Success) {

                    addTaskToLocalDb(item)

                } else if (response is Resource.Error) {

                    handleAddTaskError(response.message)

                }

            } else {

                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }

    fun updateTask(item: ToDoItemEntity) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepo.changeOnInternet(item.id, item)

                if (response is Resource.Success) {

                    updateTaskFromLocalDb(item)

                } else if (response is Resource.Error) {

                    handleUpdateTaskError(response.message)

                }

            } else {

                updateTaskFromLocalDb(item)
                handleNoInternetConnectionError()

            }
        } catch (e: Exception) {
            handleExceptionError(e.localizedMessage ?: "Unknown error")
        }
    }

    fun postSavedData(list : List<ToDoItemEntity>) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepo.addNewListToInternet(list)

                if (response is Resource.Success) {



                } else if (response is Resource.Error) {

                    handleUpdateTaskError(response.message)

                }

            } else {

                handleNoInternetConnectionError()

            }
        } catch (e: Exception) {
            handleExceptionError(e.localizedMessage ?: "Unknown error")
        }

    }

    // Обработчики

    private fun handleDeleteTaskError(errorMessage: String?) {



    }

    private fun handleAddTaskError(errorMessage: String?) {



    }

    private fun handleUpdateTaskError(errorMessage: String?) {



    }

    private fun handleNoInternetConnectionError() {



    }

    private fun handleExceptionError(errorMessage: String) {



    }

    // Работа с базой данных

    fun saveTasksToLocalDb(list: List<ToDoItemEntity>) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepo.saveDataToLocalDb(list)

    }

    fun getSavedTasks(): Flow<List<ToDoItemEntity>> {

        return toDoRepo.getSavedToDo()

    }

    fun deleteTaskFromLocalDb(id: String) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepo.deleteFromLocalDb(id)

    }

    fun addTaskToLocalDb(item: ToDoItemEntity) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepo.addToLocalDb(item)

    }

    fun updateTaskFromLocalDb(item: ToDoItemEntity) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepo.updateLocalDbItem(item)

    }

    fun getTaskById(id: String) : ToDoItemEntity? {

        var item : ToDoItemEntity? = null

        viewModelScope.launch(Dispatchers.IO) {

            item = toDoRepo.getItemById(id)

        }

        return item

    }

    // Подсчет сделанных дел

    fun incrementCounterToDo() {

        _counterToDo.value += 1

    }

    fun decrementCounterToDo() {

        _counterToDo.value -= 1

    }

}



















//    private val listener: ToDoListener = { // коррект
//
//        _todoList.value = it
//
//    }
//
//    init {
//
//        loadTasks()  // коррект
//
//        _counterToDo.value = toDoList.value?.count { it.done }!!
//
//    }
//
//    override fun onCleared() {  // коррект
//
//        super.onCleared()  // коррект
//        toDoRepo.removeListener(listener)  // коррект
//
//    }
//
//    fun loadTasks() { // можно видеть список позиций
//
//        toDoRepo.addListener(listener)
//
//    }
//
//

//
//
//    fun removeDataFromRepo(item: ToDoItem) { //удаление через Попап и свайпы
//
//        if (item.done)
//            _counterToDo.value = _counterToDo.value!! - 1
//
//        toDoRepo.removeItem(item)
//
//    }
//
//
//    fun setCheckStatusToRepo(item: ToDoItem, isChecked : Boolean) { // поставить сделанное дело
//
//        toDoRepo.setCheckStatus(item, isChecked)
//
//    }
//
//    fun getItemById(id : String) = toDoRepo.getToDoItemById(id)
//
//    fun swapElementsToRepository(item1: ToDoItem, item2: ToDoItem) {
//
//        toDoRepo.swapElements(item1, item2)
//
//    }
//
//    fun backToTheRepository(item: ToDoItem) {
//
//        toDoRepo.itemBack(item)
//
//    }