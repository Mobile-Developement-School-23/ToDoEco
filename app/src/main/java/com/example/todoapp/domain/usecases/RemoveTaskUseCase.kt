package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.TaskModel

interface RemoveTaskUseCase {
    suspend operator fun invoke(task: TaskModel)
}