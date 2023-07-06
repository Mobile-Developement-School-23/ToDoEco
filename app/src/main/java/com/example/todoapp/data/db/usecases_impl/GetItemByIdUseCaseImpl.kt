package com.example.todoapp.data.db.usecases_impl

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetItemByIdUseCaseImpl(
    private val repository: MainRepository
) : GetItemByIdUseCase {
    override operator fun invoke(id: UUID): Flow<DataState<TaskModel>> = repository.getTaskById(id)
}