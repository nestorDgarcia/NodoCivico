package com.nestorgarcia.nodocivico.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nestorgarcia.nodocivico.NodoCivicoApp
import com.nestorgarcia.nodocivico.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val db = (context.applicationContext as NodoCivicoApp).database
        val repository = ReminderRepository(db.reminderDao(), context)

        CoroutineScope(Dispatchers.IO).launch {
            repository.rescheduleAll()
        }
    }
}