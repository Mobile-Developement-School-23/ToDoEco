package com.example.todoapp.di.modules

import com.example.todoapp.data.db.DatabaseRepository
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.data.util.SharedPreferenceHelper
import com.example.todoapp.di.components.AppScope
import com.example.todoapp.domain.MainRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    @AppScope
    fun provideMainRepository(databaseSource: DatabaseRepository,
                              networkSource: NetworkRepository,
                              preferenceHelper: SharedPreferenceHelper
    ): MainRepository = MainRepository(databaseSource, networkSource, preferenceHelper)

}