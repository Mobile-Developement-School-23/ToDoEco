package com.example.todoapp.ui.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "MOVE_TO_NEXT_DAY_ACTION") {
            moveNotificationToNextDay(context, intent.getIntExtra("notificationID", 0),
            intent.getStringExtra("taskID") ?: "None",
                intent.getStringExtra("title") ?: "None",
                intent.getStringExtra("content") ?: "None")
        }
    }
    private fun moveNotificationToNextDay(context: Context, notificationID : Int, taskId : String, title: String, content: String) {
        val nextDayTimestamp = System.currentTimeMillis() + 10000
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("priority", content)
            putExtra("taskId", taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextDayTimestamp, pendingIntent)
    }
}
