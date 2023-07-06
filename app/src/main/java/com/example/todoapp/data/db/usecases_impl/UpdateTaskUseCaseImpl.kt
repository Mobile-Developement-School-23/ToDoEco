package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.TaskRepository
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.UpdateTaskUseCase

class UpdateTaskUseCaseImpl(
    private val repository: TaskRepository
) : UpdateTaskUseCase {
    override suspend operator fun invoke(task: TaskModel) =
        repository.updateTask(task)
}