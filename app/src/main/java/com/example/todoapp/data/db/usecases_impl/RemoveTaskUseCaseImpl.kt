package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.RemoveTaskUseCase

class RemoveTaskUseCaseImpl(
    private val repository: MainRepository
) : RemoveTaskUseCase {
    override suspend operator fun invoke(task: TaskModel) =
        repository.removeTask(task)
}