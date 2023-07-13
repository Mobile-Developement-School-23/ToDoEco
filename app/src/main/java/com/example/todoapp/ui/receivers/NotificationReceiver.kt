package com.example.todoapp.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.ui.util.NotificationHelper
import java.util.Random
import java.util.UUID

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Hello World!"
        val priority = intent.getStringExtra("priority") ?: "Important"
        val taskId = intent.getStringExtra("taskId") ?: "-1"
        val notificationId = UUID.fromString(taskId).hashCode()
        val timeInMillis = intent.getLongExtra("timeNotification", 0)
        NotificationHelper.showNotification(context, notificationId.toInt(), title, priority, taskId)
    }
}