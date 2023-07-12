package com.example.todoapp

import android.annotation.SuppressLint
import android.app.Application
import android.app.SharedElementCallback
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.todoapp.data.network.workers.SampleWorkerFactory
import com.example.todoapp.di.components.AppComponent
import com.example.todoapp.di.components.DaggerAppComponent
import javax.inject.Inject


class ToDoApplication : Application() {

    private var _applicationComponent: AppComponent? = null
    private lateinit var sharedPreferences : SharedPreferences
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

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setTheme()
    }

    private fun setTheme() {
        when (sharedPreferences.getString("theme", "default")) {
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "default" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        }
    }
}
