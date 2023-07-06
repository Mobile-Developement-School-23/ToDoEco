package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.TaskModel
import kotlinx.coroutines.flow.Flow

interface GetAllTasksUseCase {
    operator fun invoke(): Flow<DataState<List<TaskModel>>>
}