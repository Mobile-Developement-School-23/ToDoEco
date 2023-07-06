package com.example.todoapp.di.modules

import com.example.todoapp.domain.TaskRepository
import com.example.todoapp.data.db.DatabaseRepository
import com.example.todoapp.data.db.usecases_impl.AddTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetAllTasksUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetItemByIdUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.MergeTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.RemoveTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.UpdateTaskUseCaseImpl
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.data.util.SharedPreferenceHelper
import com.example.todoapp.domain.usecases.AddTaskUseCase
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import com.example.todoapp.domain.usecases.RemoveTaskUseCase
import com.example.todoapp.domain.usecases.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun provideTaskGetAllUseCase(repository: TaskRepository): GetAllTasksUseCase =
        GetAllTasksUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskGetByIdUseCase(repository: TaskRepository): GetItemByIdUseCase =
        GetItemByIdUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskRemoveUseCase(repository: TaskRepository): RemoveTaskUseCase =
        RemoveTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskUpdateUseCase(repository: TaskRepository): UpdateTaskUseCase =
        UpdateTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskAddUseCase(repository: TaskRepository): AddTaskUseCase =
        AddTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideMergeUseCase(repository: TaskRepository): MergeTasksUseCase =
        MergeTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskRepository(
        dataSource: DatabaseRepository,
        networkSource: NetworkRepository,
        committer: SharedPreferenceHelper
    ): TaskRepository =
        TaskRepository(dataSource, networkSource, committer)
}