package com.example.todoapp.domain.usecases

import com.example.todoapp.domain.DataState
import com.example.todoapp.domain.TaskModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface GetItemByIdUseCase {
    operator fun invoke(id: UUID): Flow<DataState<TaskModel>>
}