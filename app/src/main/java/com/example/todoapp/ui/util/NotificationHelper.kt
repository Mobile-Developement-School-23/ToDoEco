package com.example.todoapp.ui.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.R
import com.example.todoapp.domain.Importance
import com.example.todoapp.domain.TaskModel
import com.example.todoapp.ui.activity.MainActivity
import com.example.todoapp.ui.receivers.NotificationButtonReceiver
import com.example.todoapp.ui.receivers.NotificationReceiver
import java.util.Calendar

object NotificationHelper {
    private const val CHANNEL_ID = "my_channel_id"
    private const val CHANNEL_NAME = "My Channel"
    private const val CHANNEL_DESCRIPTION = "My Channel Description"
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        content: String,
        taskId: String
    ) {
        createNotificationChannel(context)
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra("openFragment", true)
        notificationIntent.putExtra("taskIDForFragment", taskId)
        notificationIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val buttonIntent = Intent(context, NotificationButtonReceiver::class.java)
        buttonIntent.action = "MOVE_TO_NEXT_DAY_ACTION"
        buttonIntent.putExtra("notificationID", notificationId)
        buttonIntent.putExtra("taskID", taskId)
        buttonIntent.putExtra("title", title)
        buttonIntent.putExtra("content", content)
        val pendingIntent2 = PendingIntent.getBroadcast(context, taskId.hashCode(),
            buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.plant_svgrepo_com)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_menu_gallery, "Shift1Day", pendingIntent2)
            .setContentIntent(pendingIntent)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    fun deleteNotification(context: Context, task: TaskModel) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", task.text)
            when (task.priority) {
                Importance.LOW -> putExtra("priority", "Low importance")
                Importance.BASIC -> putExtra("priority", "Basic importance")
                Importance.IMPORTANT -> putExtra("priority", "High importance")
                else -> putExtra("priority", "Basic importance")
            }
            putExtra("taskId", task.id.toString())
        }
        val pending = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}
