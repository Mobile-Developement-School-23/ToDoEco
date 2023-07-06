package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.network.observers.NetworkConnectivityObserver
import com.example.todoapp.domain.usecases.AddTaskUseCase
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import com.example.todoapp.domain.usecases.RemoveTaskUseCase
import com.example.todoapp.domain.usecases.UpdateTaskUseCase

class ViewModelFactory(
    private val updateCase: UpdateTaskUseCase,
    private val getAllCase: GetAllTasksUseCase,
    private val getSingleCase: GetItemByIdUseCase,
    private val removeCase: RemoveTaskUseCase,
    private val addCase: AddTaskUseCase,
    private val mergeCase: MergeTasksUseCase,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    updateCase = updateCase,
                    getAllCase = getAllCase,
                    getSingleCase = getSingleCase,
                    removeCase = removeCase,
                    addCase = addCase,
                    mergeCase = mergeCase,
                    connectivityObserver = connectivityObserver
                ) as T
            }
            modelClass.isAssignableFrom(EditAddViewModel::class.java) -> {
                EditAddViewModel(
                    updateCase = updateCase,
                    getAllCase = getAllCase,
                    getSingleCase = getSingleCase,
                    removeCase = removeCase,
                    addCase = addCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
