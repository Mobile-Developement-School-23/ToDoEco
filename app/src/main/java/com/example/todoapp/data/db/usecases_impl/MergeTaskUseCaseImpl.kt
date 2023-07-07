package com.example.todoapp.data.db.usecases_impl

import android.util.Log
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.MainRepository
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MergeTaskUseCaseImpl (
    private val repository: MainRepository
) : MergeTasksUseCase {
    override suspend fun invoke(): Flow<DataState<Boolean>> = flow {
        emit(DataState.Initial)
        repository.mergeTasks()
        emit(DataState.Result(true))
    }.catch {
        emit(DataState.Exception(it))
    }
}