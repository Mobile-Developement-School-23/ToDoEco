package com.example.todoapp.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.todoapp.ToDoApplication
import com.example.todoapp.data.ToDoRepository
import com.example.todoapp.ui.viewmodels.EditAddViewModel
import com.example.todoapp.ui.viewmodels.HomeViewModel


class ViewModelFactory(
    private val app: ToDoApplication
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       val viewModel = when (modelClass) {
            HomeViewModel::class.java -> {
                HomeViewModel(app, app.toDoRepository)
            }
            EditAddViewModel::class.java -> {
                EditAddViewModel(app.toDoRepository)
            }
           else -> {
               throw IllegalStateException("Unknown view model class")
           }
        }
        return viewModel as T
    }

}

//class MyWorkerFactory(private val repository: ToDoRepository) : WorkerFactory() {
//    override fun createWorker(
//        appContext: Context,
//        workerClassName: String,
//        workerParameters: WorkerParameters
//    ): ListenableWorker? {
//        return when (workerClassName) {
//            ServerUpdateWorker::class.java.name -> {
//                ServerUpdateWorker(appContext, workerParameters, repository)
//            }
//            else -> null
//        }
//    }
//}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as ToDoApplication)
