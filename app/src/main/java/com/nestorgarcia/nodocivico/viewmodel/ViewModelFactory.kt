package com.nestorgarcia.nodocivico.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nestorgarcia.nodocivico.NodoCivicoApp
import com.nestorgarcia.nodocivico.repository.CategoryRepository
import com.nestorgarcia.nodocivico.repository.ReminderRepository
import com.nestorgarcia.nodocivico.repository.ReportRepository
import com.nestorgarcia.nodocivico.repository.SyncRepository
import com.nestorgarcia.nodocivico.repository.UserRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val db by lazy {
        (context.applicationContext as NodoCivicoApp).database
    }

    private val userRepository by lazy {
        UserRepository(db.userDao())
    }

    private val categoryRepository by lazy {
        CategoryRepository(db.categoryDao())
    }

    private val reportRepository by lazy {
        ReportRepository(db.reportDao(), db.reportStatusHistoryDao())
    }

    private val reminderRepository by lazy {
        ReminderRepository(db.reminderDao(), context)
    }

    private val syncRepository by lazy {
        SyncRepository(db.syncEventDao(), db.reportDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(userRepository) as T
            modelClass.isAssignableFrom(ReportViewModel::class.java) ->
                ReportViewModel(reportRepository) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) ->
                CategoryViewModel(categoryRepository) as T
            modelClass.isAssignableFrom(ReminderViewModel::class.java) ->
                ReminderViewModel(reminderRepository) as T
            modelClass.isAssignableFrom(SyncViewModel::class.java) ->
                SyncViewModel(syncRepository) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}