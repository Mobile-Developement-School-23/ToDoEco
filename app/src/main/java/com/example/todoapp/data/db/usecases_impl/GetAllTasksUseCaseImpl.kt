package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import kotlinx.coroutines.flow.Flow

class GetAllTasksUseCaseImpl(
    private val repository: MainRepository
) : GetAllTasksUseCase {
    override operator fun invoke(): Flow<DataState<List<TaskModel>>> = repository.getAllTasks()
}