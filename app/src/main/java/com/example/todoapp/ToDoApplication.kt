package com.example.todoapp

import android.annotation.SuppressLint
import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.di.components.AppComponent
import com.example.todoapp.di.components.DaggerAppComponent
import com.example.todoapp.di.modules.AppModule
import com.example.todoapp.workers.ServerUpdateWorker
import java.util.concurrent.TimeUnit


class ToDoApplication : Application() {

    lateinit var appComponent: AppComponent
    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate() {
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<ServerUpdateWorker>(8, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

    }

}
