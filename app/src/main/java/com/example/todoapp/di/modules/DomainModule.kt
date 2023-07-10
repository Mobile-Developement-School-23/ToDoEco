package com.example.todoapp.di.modules

import com.example.todoapp.domain.MainRepository
import com.example.todoapp.data.db.usecases_impl.AddTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetAllTasksUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.GetItemByIdUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.MergeTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.RemoveTaskUseCaseImpl
import com.example.todoapp.data.db.usecases_impl.UpdateTaskUseCaseImpl
import com.example.todoapp.di.components.ActivityScope
import com.example.todoapp.di.components.AppScope
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
    @ActivityScope
    fun provideTaskGetAllUseCase(repository: MainRepository): GetAllTasksUseCase =
        GetAllTasksUseCaseImpl(repository)

    @Provides
    @ActivityScope
    fun provideTaskGetByIdUseCase(repository: MainRepository): GetItemByIdUseCase =
        GetItemByIdUseCaseImpl(repository)

    @Provides
    @ActivityScope
    fun provideTaskRemoveUseCase(repository: MainRepository): RemoveTaskUseCase =
        RemoveTaskUseCaseImpl(repository)

    @Provides
    @ActivityScope
    fun provideTaskUpdateUseCase(repository: MainRepository): UpdateTaskUseCase =
        UpdateTaskUseCaseImpl(repository)

    @Provides
    @ActivityScope
    fun provideTaskAddUseCase(repository: MainRepository): AddTaskUseCase =
        AddTaskUseCaseImpl(repository)

    @Provides
    @ActivityScope
    fun provideMergeUseCase(repository: MainRepository): MergeTasksUseCase =
        MergeTaskUseCaseImpl(repository)

}