package com.example.todoapp.data.network.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

class SampleWorkerFactory @Inject constructor(
    private val helloWorldWorkerFactory: ServerUpdateWorker.Factory,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            ServerUpdateWorker::class.java.name ->
                helloWorldWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}