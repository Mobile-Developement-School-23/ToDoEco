package com.example.todoapp

import android.annotation.SuppressLint
import android.app.Application
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.network.workers.SampleWorkerFactory
import com.example.todoapp.di.components.AppComponent
import com.example.todoapp.di.components.DaggerAppComponent
import com.example.todoapp.di.modules.AppModule
import com.example.todoapp.data.network.workers.ServerUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ToDoApplication : Application() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var sampleWorkerFactory: SampleWorkerFactory

    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate() {
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.injectTo(this)
        super.onCreate()

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(sampleWorkerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)

    }

}
