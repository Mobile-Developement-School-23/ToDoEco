//package com.example.todoapp.workers
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.example.todoapp.data.ToDoRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class ServerUpdateWorker(
//    appContext: Context,
//    workerParams: WorkerParameters,
//    private val toDoRepository: ToDoRepository
//) : CoroutineWorker(appContext, workerParams) {
//
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        try {
//            if (hasInternetConnection()) {
//
//                val result = toDoRepository.getToDoItems()
//
//            }
//        } catch (e: Exception) {
//            return@withContext Result.failure()
//        }
//
//        return@withContext Result.failure()
//    }
//
//    private fun hasInternetConnection(): Boolean {
//        val connectivityManager =
//            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkCapabilities = connectivityManager.activeNetwork ?: return false
//        val capabilities =
//            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
//
//        return when {
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            else -> false
//        }
//    }
//}