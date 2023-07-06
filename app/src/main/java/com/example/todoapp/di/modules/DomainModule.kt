package com.example.todoapp.di.modules

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.data.db.usecases_impl.AddTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetAllTasksUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetItemByIdUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.MergeTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.RemoveTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.UpdateTaskUseCaseImpl
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
    fun provideTaskGetAllUseCase(repository: MainRepository): GetAllTasksUseCase =
        GetAllTasksUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskGetByIdUseCase(repository: MainRepository): GetItemByIdUseCase =
        GetItemByIdUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskRemoveUseCase(repository: MainRepository): RemoveTaskUseCase =
        RemoveTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskUpdateUseCase(repository: MainRepository): UpdateTaskUseCase =
        UpdateTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideTaskAddUseCase(repository: MainRepository): AddTaskUseCase =
        AddTaskUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideMergeUseCase(repository: MainRepository): MergeTasksUseCase =
        MergeTaskUseCaseImpl(repository)

}