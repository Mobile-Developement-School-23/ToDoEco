package com.example.todoapp.workers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.todoapp.domain.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider
class ServerUpdateWorker
    @AssistedInject constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        toDoRepository: TaskRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}