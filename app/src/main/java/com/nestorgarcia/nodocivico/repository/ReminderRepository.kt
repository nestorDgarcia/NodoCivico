package com.nestorgarcia.nodocivico.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.nestorgarcia.nodocivico.data.local.dao.ReminderDao
import com.nestorgarcia.nodocivico.model.Reminder
import com.nestorgarcia.nodocivico.receiver.ReminderReceiver

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val context: Context
) {

    fun getAll(): LiveData<List<Reminder>> = reminderDao.getAll()

    fun getByReport(reportId: Int): LiveData<List<Reminder>> =
        reminderDao.getByReport(reportId)

    suspend fun getPendingReminders(): List<Reminder> =
        reminderDao.getPendingReminders()

    suspend fun getById(id: Int): Reminder? = reminderDao.getById(id)

    suspend fun insert(reminder: Reminder): Long {
        val id = reminderDao.insert(reminder)
        scheduleAlarm(reminder.copy(id = id.toInt()))
        return id
    }

    suspend fun delete(reminder: Reminder) {
        cancelAlarm(reminder)
        reminderDao.delete(reminder)
    }

    suspend fun deleteById(id: Int) {
        val reminder = reminderDao.getById(id) ?: return
        cancelAlarm(reminder)
        reminderDao.deleteById(id)
    }

    suspend fun markTriggered(id: Int) = reminderDao.markTriggered(id)

    // Reprogramar todas las alarmas pendientes (usado por BootReceiver)
    suspend fun rescheduleAll() {
        val pending = reminderDao.getPendingReminders()
        pending.forEach { scheduleAlarm(it) }
    }

    private fun scheduleAlarm(reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.nestorgarcia.nodocivico.ACTION_REMINDER_TRIGGER"
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_message", reminder.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.triggerAt,
            pendingIntent
        )
    }

    private fun cancelAlarm(reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}