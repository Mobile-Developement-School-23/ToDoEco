package com.example.todoapp.di.components

import android.content.Context
import com.example.todoapp.ToDoApplication
import com.example.todoapp.di.modules.DatabaseModule
import com.example.todoapp.di.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
@Scope
annotation class AppScope

@AppScope
@Component(modules = [DatabaseModule::class, NetworkModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun activityComponent(): ActivityComponent

//    fun inject(fragment: HomeFragment)
//    fun inject(fragment: EditAddFragment)
//    fun inject(fragment: InfoFragment)
//    fun inject(worker: ServerUpdateWorker)
    fun injectTo(application: ToDoApplication)
}