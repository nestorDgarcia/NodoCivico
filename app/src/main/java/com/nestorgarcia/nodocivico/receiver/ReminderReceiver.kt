package com.nestorgarcia.nodocivico.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nestorgarcia.nodocivico.MainActivity
import com.nestorgarcia.nodocivico.NodoCivicoApp
import com.nestorgarcia.nodocivico.R
import com.nestorgarcia.nodocivico.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "nodo_civico_reminders"
        const val CHANNEL_NAME = "Recordatorios Nodo Cívico"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", -1)
        val message = intent.getStringExtra("reminder_message") ?: "Tienes un recordatorio pendiente"

        createNotificationChannel(context)
        showNotification(context, reminderId, message)

        if (reminderId != -1) {
            val db = (context.applicationContext as NodoCivicoApp).database
            val repository = ReminderRepository(db.reminderDao(), context)
            CoroutineScope(Dispatchers.IO).launch {
                repository.markTriggered(reminderId)
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Recordatorios de seguimiento de reportes"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun showNotification(context: Context, id: Int, message: String) {
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, id, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Nodo Cívico — Recordatorio")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }
}