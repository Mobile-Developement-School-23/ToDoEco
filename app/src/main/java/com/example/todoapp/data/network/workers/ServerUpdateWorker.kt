package com.example.todoapp.data.network.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.ToDoApplication
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class ServerUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val mergeCase: MergeTasksUseCase
) : CoroutineWorker(appContext, workerParams) {

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


    /**
     * class annotate with @AssistedFactory will available in the dependency graph, you don't need
     * additional binding from [HelloWorldWorker_Factory_Impl] to [Factory].
     */
    @AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): ServerUpdateWorker
    }

}