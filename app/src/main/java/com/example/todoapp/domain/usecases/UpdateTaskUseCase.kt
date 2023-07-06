package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.exceptions.ValidationException

interface UpdateTaskUseCase {
    @Throws(ValidationException::class)
    suspend operator fun invoke(task: TaskModel)
}