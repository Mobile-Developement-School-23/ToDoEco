package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.TaskRepository
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.usecases.AddTaskUseCase

class AddTaskUseCaseImpl(
    private val repository: TaskRepository
) : AddTaskUseCase {
    override suspend fun invoke(text: String, priority: Importance, deadline: Long?) =
        repository.addTask(text, priority, deadline)
}