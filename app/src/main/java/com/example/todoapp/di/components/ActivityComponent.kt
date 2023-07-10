package com.example.todoapp.di.components

import com.example.todoapp.data.network.workers.ServerUpdateWorker
import com.example.todoapp.di.modules.DomainModule
import com.example.todoapp.di.modules.ViewModelModule
import com.example.todoapp.ui.fragments.EditAddFragment
import com.example.todoapp.ui.fragments.HomeFragment
import com.example.todoapp.ui.fragments.InfoFragment
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class ActivityScope

@ActivityScope
@Subcomponent(modules = ([DomainModule::class, ViewModelModule::class]))
interface ActivityComponent {

    fun inject(fragment: HomeFragment)
    fun inject(fragment: EditAddFragment)
    fun inject(fragment: InfoFragment)
    fun inject(worker: ServerUpdateWorker)

}