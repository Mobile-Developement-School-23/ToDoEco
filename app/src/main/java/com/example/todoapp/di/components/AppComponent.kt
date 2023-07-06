package com.example.todoapp.di.components

import com.example.todoapp.ToDoApplication
import com.example.todoapp.di.modules.AppModule
import com.example.todoapp.di.modules.DatabaseModule
import com.example.todoapp.di.modules.DomainModule
import com.example.todoapp.di.modules.NetworkModule
import com.example.todoapp.ui.fragments.EditAddFragment
import com.example.todoapp.ui.fragments.HomeFragment
import com.example.todoapp.ui.fragments.InfoFragment
import com.example.todoapp.workers.ServerUpdateWorker
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [], modules = [AppModule::class, DatabaseModule::class, DomainModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(fragment: HomeFragment)
    fun inject(fragment: EditAddFragment)
    fun inject(fragment: InfoFragment)
    fun inject(worker: ServerUpdateWorker)
}