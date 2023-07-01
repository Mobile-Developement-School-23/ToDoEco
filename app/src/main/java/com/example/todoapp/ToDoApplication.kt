package com.example.todoapp

import android.app.Application
import com.example.todoapp.data.ToDoRepository
import com.example.todoapp.db.ToDoDatabase


// , Configuration.Provider

class ToDoApplication : Application() {

    lateinit var toDoRepository: ToDoRepository

    override fun onCreate() {
        super.onCreate()
        toDoRepository = ToDoRepository(ToDoDatabase(context = applicationContext), applicationContext)
//        schedulePeriodicWork()
    }

//    private fun schedulePeriodicWork() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val repeatingRequest = PeriodicWorkRequestBuilder<ServerUpdateWorker>(
//            repeatInterval = 8,
//            repeatIntervalTimeUnit = TimeUnit.HOURS
//        )
//            .setConstraints(constraints)
//            .build()
//
//        val workManager = WorkManager.getInstance(applicationContext)
//        workManager.enqueueUniquePeriodicWork(
//            "SERVER_UPDATE_WORK",
//            ExistingPeriodicWorkPolicy.REPLACE,
//            repeatingRequest
//        )
//    }

//    override fun getWorkManagerConfiguration(): Configuration {
//        val workerFactory = MyWorkerFactory(toDoRepository)
//        return Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .build()
//    }
}
