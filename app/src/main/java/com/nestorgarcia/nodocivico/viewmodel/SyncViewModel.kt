package com.nestorgarcia.nodocivico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorgarcia.nodocivico.model.SyncEvent
import com.nestorgarcia.nodocivico.repository.SyncRepository
import kotlinx.coroutines.launch

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}

class SyncViewModel(private val syncRepository: SyncRepository) : ViewModel() {

    private val _syncState = MutableLiveData<SyncState>(SyncState.Idle)
    val syncState: LiveData<SyncState> = _syncState

    val allEvents: LiveData<List<SyncEvent>> = syncRepository.getAll()
    val failedEvents: LiveData<List<SyncEvent>> = syncRepository.getFailed()
    val failedCount: LiveData<Int> = syncRepository.getFailedCount()
    val pendingCount: LiveData<Int> = syncRepository.getPendingCount()
    val syncedCount: LiveData<Int> = syncRepository.getSyncedCount()

    fun clearHistory() {
        viewModelScope.launch {
            try {
                syncRepository.clearAll()
            } catch (e: Exception) {
                _syncState.value = SyncState.Error("Error al limpiar historial")
            }
        }
    }

    fun resetState() {
        _syncState.value = SyncState.Idle
    }
}