package com.example.todoapp.di.modules

import com.example.todoapp.data.network.observers.NetworkConnectivityObserver
import com.example.todoapp.di.components.ActivityScope
import com.example.todoapp.domain.usecases.AddTaskUseCase
import com.example.todoapp.domain.usecases.GetAllTasksUseCase
import com.example.todoapp.domain.usecases.GetItemByIdUseCase
import com.example.todoapp.domain.usecases.MergeTasksUseCase
import com.example.todoapp.domain.usecases.RemoveTaskUseCase
import com.example.todoapp.domain.usecases.UpdateTaskUseCase
import com.example.todoapp.ui.viewmodels.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule(

) {

    @Provides
    @ActivityScope
    fun provideTaskViewModelFactory(
        editCase: UpdateTaskUseCase,
        getAllCase: GetAllTasksUseCase,
        getSingleCase: GetItemByIdUseCase,
        removeCase: RemoveTaskUseCase,
        addCase: AddTaskUseCase,
        mergeCase: MergeTasksUseCase,
        connectivityObserver: NetworkConnectivityObserver
    ): ViewModelFactory = ViewModelFactory(
        updateCase = editCase,
        getAllCase = getAllCase,
        getSingleCase = getSingleCase,
        removeCase = removeCase,
        addCase = addCase,
        mergeCase = mergeCase,
        connectivityObserver = connectivityObserver
    )

}