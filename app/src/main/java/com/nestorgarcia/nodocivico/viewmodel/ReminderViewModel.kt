package com.nestorgarcia.nodocivico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorgarcia.nodocivico.model.Reminder
import com.nestorgarcia.nodocivico.repository.ReminderRepository
import kotlinx.coroutines.launch

sealed class ReminderState {
    object Idle : ReminderState()
    object Loading : ReminderState()
    object Success : ReminderState()
    data class Error(val message: String) : ReminderState()
}

class ReminderViewModel(private val reminderRepository: ReminderRepository) : ViewModel() {

    private val _reminderState = MutableLiveData<ReminderState>(ReminderState.Idle)
    val reminderState: LiveData<ReminderState> = _reminderState

    val allReminders: LiveData<List<Reminder>> = reminderRepository.getAll()

    fun getByReport(reportId: Int): LiveData<List<Reminder>> =
        reminderRepository.getByReport(reportId)

    fun insert(reportId: Int, message: String, triggerAt: Long) {
        _reminderState.value = ReminderState.Loading
        viewModelScope.launch {
            try {
                val reminder = Reminder(
                    reportId = reportId,
                    message = message,
                    triggerAt = triggerAt,
                    alarmRequestCode = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                )
                reminderRepository.insert(reminder)
                _reminderState.value = ReminderState.Success
            } catch (e: Exception) {
                _reminderState.value = ReminderState.Error("Error al crear el recordatorio")
            }
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            try {
                reminderRepository.delete(reminder)
            } catch (e: Exception) {
                _reminderState.value = ReminderState.Error("Error al eliminar el recordatorio")
            }
        }
    }

    fun markTriggered(id: Int) {
        viewModelScope.launch {
            reminderRepository.markTriggered(id)
        }
    }

    fun resetState() {
        _reminderState.value = ReminderState.Idle
    }
}