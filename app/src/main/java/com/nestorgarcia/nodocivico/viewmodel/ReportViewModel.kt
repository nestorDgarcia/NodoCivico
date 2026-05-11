package com.nestorgarcia.nodocivico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorgarcia.nodocivico.model.Report
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.repository.ReportRepository
import kotlinx.coroutines.launch

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}

class ReportViewModel(private val reportRepository: ReportRepository) : ViewModel() {

    private val _reportState = MutableLiveData<ReportState>(ReportState.Idle)
    val reportState: LiveData<ReportState> = _reportState

    val allReports: LiveData<List<Report>> = reportRepository.getAll()
    val pendingCount: LiveData<Int> = reportRepository.getPendingCount()
    val syncedCount: LiveData<Int> = reportRepository.getSyncedCount()

    private val _currentFilter = MutableLiveData<ReportStatus?>(null)
    val currentFilter: LiveData<ReportStatus?> = _currentFilter

    fun getById(id: Int): LiveData<Report?> = reportRepository.getById(id)

    fun getByStatus(status: ReportStatus): LiveData<List<Report>> =
        reportRepository.getByStatus(status)

    fun getStatusHistory(reportId: Int) =
        reportRepository.getStatusHistory(reportId)

    fun setFilter(status: ReportStatus?) {
        _currentFilter.value = status
    }

    fun insert(report: Report) {
        _reportState.value = ReportState.Loading
        viewModelScope.launch {
            try {
                reportRepository.insert(report)
                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error("Error al guardar el reporte")
            }
        }
    }

    fun update(report: Report) {
        _reportState.value = ReportState.Loading
        viewModelScope.launch {
            try {
                reportRepository.update(report)
                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error("Error al actualizar el reporte")
            }
        }
    }

    fun updateStatus(id: Int, status: ReportStatus, note: String? = null) {
        viewModelScope.launch {
            try {
                reportRepository.updateStatus(id, status, note)
                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error("Error al actualizar el estado")
            }
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            try {
                reportRepository.deleteById(id)
            } catch (e: Exception) {
                _reportState.value = ReportState.Error("Error al eliminar el reporte")
            }
        }
    }

    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}