package com.example.todoapp

import android.annotation.SuppressLint
import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.todoapp.data.network.workers.SampleWorkerFactory
import com.example.todoapp.di.components.AppComponent
import com.example.todoapp.di.components.DaggerAppComponent
import javax.inject.Inject


class ToDoApplication : Application() {

    private var _applicationComponent: AppComponent? = null
    val applicationComponent: AppComponent get() = requireNotNull(_applicationComponent!!) {
        "AppComponent must not be null!"
    }

    @Inject
    lateinit var sampleWorkerFactory: SampleWorkerFactory

    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate() {
        _applicationComponent = DaggerAppComponent.factory().create(this)
        applicationComponent.injectTo(this)
        super.onCreate()

        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(sampleWorkerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)

    }

}
