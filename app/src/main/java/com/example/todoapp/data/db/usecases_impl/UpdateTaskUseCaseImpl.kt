package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.UpdateTaskUseCase

class UpdateTaskUseCaseImpl(
    private val repository: MainRepository
) : UpdateTaskUseCase {
    override suspend operator fun invoke(task: TaskModel) =
        repository.updateTask(task)
}