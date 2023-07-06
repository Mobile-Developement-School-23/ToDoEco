package com.example.todoapp

import android.annotation.SuppressLint
import android.app.Application
import com.example.todoapp.di.components.AppComponent
import com.example.todoapp.di.components.DaggerAppComponent
import com.example.todoapp.di.modules.AppModule


class ToDoApplication : Application() {

    lateinit var appComponent: AppComponent
    @SuppressLint("SimpleDateFormat", "HardwareIds")
    override fun onCreate() {

        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

    }

}
