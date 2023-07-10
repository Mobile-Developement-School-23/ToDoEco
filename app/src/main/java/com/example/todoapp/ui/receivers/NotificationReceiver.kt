package com.example.todoapp.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.ui.util.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title") ?: "Hello World!"
        val priority = intent.getStringExtra("priority") ?: "Important"
        NotificationHelper.showNotification(context,1, title, priority)

    }
}