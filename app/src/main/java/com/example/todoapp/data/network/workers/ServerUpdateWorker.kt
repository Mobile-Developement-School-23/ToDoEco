package com.example.todoapp.data.network.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.ToDoApplication
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import javax.inject.Inject

class ServerUpdateWorker(
        private val appContext: Context,
        private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var mergeCase: MergeTasksUseCase

    init {
        (appContext.applicationContext as ToDoApplication).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        mergeCase().collect { state ->
            when (state) {
                is DataState.Result -> {}
                is DataState.Exception -> {}
                else -> {}
            }
        }
        return Result.success()
    }



}