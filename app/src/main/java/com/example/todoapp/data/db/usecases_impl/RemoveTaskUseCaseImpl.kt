package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.TaskRepository
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.RemoveTaskUseCase

class RemoveTaskUseCaseImpl(
    private val repository: TaskRepository
) : RemoveTaskUseCase {
    override suspend operator fun invoke(task: TaskModel) =
        repository.removeTask(task)
}