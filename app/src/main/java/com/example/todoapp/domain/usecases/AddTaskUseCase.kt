package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.Importance

interface AddTaskUseCase {
    suspend operator fun invoke(text: String, priority: Importance, deadline: Long?)
}