package com.example.todoapp.data

import android.content.Context
import android.util.Log
import com.example.todoapp.api.RetrofitInstance
import com.example.todoapp.api.request_response_data.OneToDoItemResponse
import com.example.todoapp.api.request_response_data.TaskListRequest
import com.example.todoapp.api.request_response_data.TaskListResponse
import com.example.todoapp.api.request_response_data.toEntityList
import com.example.todoapp.db.ToDoDatabase
import com.example.todoapp.db.ToDoItemEntity
import com.example.todoapp.db.toResponseList
import com.example.todoapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.util.UUID

class ToDoRepository(

    private val db: ToDoDatabase, private val context : Context

) {

    private val sharedPreferencesHelper : SharedPreferencesHelper =
        SharedPreferencesHelper(context = context)

    // Взаимодействия с сетью

    fun getToDoItems(): Flow<Resource<List<ToDoItemEntity>>> = flow {

//      emit(Resource.Loading())

        try {

            val response = RetrofitInstance.api.getList()

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    saveDataToLocalDb(resultResponse.list.toEntityList())

                    sharedPreferencesHelper.addInt("LAST_REVISION", resultResponse.revision)
                    Log.d("POLOSHILI_REVISIU", sharedPreferencesHelper
                        .getInt("LAST_REVISION").toString())

                    emit(Resource.Success(resultResponse.list.toEntityList()))

                } else {

                    emit(Resource.Error("Empty response body"))

                }

            } else {

                emit(Resource.Error("Request failed with ${response.code()}: ${response.message()}"))

            }

        } catch (e: Exception) {

            emit(Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}"))

        }

    }.flowOn(Dispatchers.IO)

    suspend fun deleteFromInternet(id: String): Resource<OneToDoItemResponse> {

        return try {

            val response = RetrofitInstance.api.deleteListItem(
                sharedPreferencesHelper
                .getInt("LAST_REVISION"),
                UUID.fromString(id))

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    suspend fun addToInternet(newPosition: ToDoItemEntity): Resource<OneToDoItemResponse> {

        return try {

            Log.d("REVISIA", sharedPreferencesHelper
                .getInt("LAST_REVISION").toString())
            Log.d("OSHIBKA", newPosition.toString())

            val response = RetrofitInstance.api.addItemToList(sharedPreferencesHelper
                .getInt("LAST_REVISION"), newPosition.toRequest())

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Log.d("OSHIBKA", response.message())

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }
        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    suspend fun addNewListToInternet(newList : List<ToDoItemEntity>) : Resource<TaskListResponse> {

        return try {

            val response = RetrofitInstance.api.updateList(sharedPreferencesHelper
                .getInt("LAST_REVISION"), TaskListRequest("ok", newList.toResponseList()) )

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Log.d("OSHIBKA", response.message())

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }
        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    suspend fun changeOnInternet(id: String, changePosition: ToDoItemEntity): Resource<OneToDoItemResponse> {

        return try {

            val response = RetrofitInstance.api.changeListItem(sharedPreferencesHelper.getInt("LAST_REVISION"),
                UUID.fromString(id),
                changePosition.toRequest())

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }


    // Взаимодействия с локальной БД

    suspend fun saveDataToLocalDb(tasks: List<ToDoItemEntity>) = db.getToDoDao().insertItems(tasks)

    fun getSavedToDo(): Flow<List<ToDoItemEntity>> = db.getToDoDao().getAllItems()

    suspend fun deleteFromLocalDb(id: String) {

        db.getToDoDao().deleteItemById(id)
        deleteFromInternet(id)

    }

    suspend fun addToLocalDb(item: ToDoItemEntity) {

        db.getToDoDao().insertItem(item)
        addToInternet(item)

    }

    suspend fun updateLocalDbItem(item: ToDoItemEntity) {

        db.getToDoDao().updateItem(item)
        changeOnInternet(item.id, item)
    }

    suspend fun getItemById(id : String) = db.getToDoDao().getItemById(id)

}






















//    private var todoList: MutableList<ToDoItem> = mutableListOf() // коррект
//
//    private val listeners = mutableSetOf<ToDoListener>()
//
//    private var _id: String = "1" // коррект
//    val id : String get() = _id // коррект
//
//    init { // коррект
//
//        addItem(ToDoItem("1", "Complete project report", ToDoItem.Importance.NORMAL, Date(), false, Date(), null))
//        addItem(ToDoItem("2", "Buy groceries", ToDoItem.Importance.LOW, null, false, Date(), null))
//        addItem(ToDoItem("3", "Call client", ToDoItem.Importance.URGENT, null, false, Date(), null))
//        addItem(ToDoItem("4", "Pay bills", ToDoItem.Importance.NORMAL, Date(), true, Date(), Date()))
//        addItem(ToDoItem("5", "Schedule appointment", ToDoItem.Importance.LOW, null, false, Date(), null))
//        addItem(ToDoItem("6", "Submit expense report", ToDoItem.Importance.URGENT, Date(), false, Date(), null))
//        addItem(ToDoItem("7", "Read book", ToDoItem.Importance.LOW, null, false, Date(), null))
//        addItem(ToDoItem("8", "Attend team meeting", ToDoItem.Importance.NORMAL, Date(), true, Date(), Date()))
//        addItem(ToDoItem("9", "Clean the house", ToDoItem.Importance.LOW, null, false, Date(), null))
//        addItem(ToDoItem("10", "Exercise", ToDoItem.Importance.NORMAL, Date(), false, Date(), null))
//
//    }
//
//    fun getToDoList(): List<ToDoItem> { // коррект
//
//        return todoList // коррект
//
//    }
//
//
//    fun addItem(toDoItem: ToDoItem) { // коррект
//
//        // добавление нового элемента полностью
//
//        toDoItem.id = this.id
//
//        todoList = ArrayList(todoList)
//        todoList.add(toDoItem) // коррект
//
//        val number = _id.toInt()
//        val incrementedNumber = number + 1
//        this._id = incrementedNumber.toString()
//
//    } // коррект
//
//    fun removeItem(toDoItem: ToDoItem) { // коррект
//
//        val indexToDelete = todoList.indexOfFirst { it.id == toDoItem.id }
//
//        if (indexToDelete != -1) {
//
//            todoList = ArrayList(todoList)
//            todoList.removeAt(indexToDelete)
//            notifyChanges()
//
//        }
//
//    }
//
//    fun saveData(toDoItem: ToDoItem) {
//
//        val index = todoList.indexOfFirst { it.id == toDoItem.id }
//
//        if (index != -1) {
//
//            todoList = ArrayList(todoList)
//            todoList[index] = toDoItem
//            notifyChanges()
//
//        }
//
//    }
//
//    fun setCheckStatus(item: ToDoItem, isChecked: Boolean) {
//
//        val index = todoList.indexOfFirst { it.id == item.id }
//
//        if (index != -1) {
//
//            todoList = ArrayList(todoList)
//            todoList[index].done = isChecked
//            notifyChanges()
//
//        }
//
//        notifyChanges()
//
//    }
//
//    fun addListener(listener: ToDoListener) {
//
//        listeners.add(listener)
//        listener.invoke(todoList)
//
//    }
//
//    fun removeListener(listener: ToDoListener) {
//
//        listeners.remove(listener)
//
//    }
//
//    private fun notifyChanges() {
//
//        listeners.forEach { it.invoke(todoList) }
//
//    }
//
//     fun getToDoItemById(id : String) : ToDoItem {
//
//        val index = todoList.indexOfFirst { it.id == id }
//
//         val toDoItem : ToDoItem
//         if (index != -1) {
//
//             toDoItem = todoList[index]
//             return toDoItem
//
//         }
//         else
//             throw TaskNotFoundException()
//
//    }
//
//    fun swapElements(item1: ToDoItem, item2: ToDoItem) {
//
//        val index1 = todoList.indexOfFirst { it.id == item1.id }
//        val index2 = todoList.indexOfFirst { it.id == item2.id }
//
//        todoList = ArrayList(todoList)
//        todoList[index1] = item2
//        todoList[index2] = item1
//
//        notifyChanges()
//
//    }
//
//    fun itemBack(item: ToDoItem) {
//
//        todoList = ArrayList(todoList)
//        todoList.add(0, item)
//
//        notifyChanges()
//
//    }