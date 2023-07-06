package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.DataState
import kotlinx.coroutines.flow.Flow

interface MergeTasksUseCase {
    operator suspend fun invoke(): Flow<DataState<Boolean>>
}