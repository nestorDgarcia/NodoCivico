package com.nestorgarcia.nodocivico.repository

import androidx.lifecycle.LiveData
import com.nestorgarcia.nodocivico.data.local.dao.ReportDao
import com.nestorgarcia.nodocivico.data.local.dao.ReportStatusHistoryDao
import com.nestorgarcia.nodocivico.model.Report
import com.nestorgarcia.nodocivico.model.ReportStatus
import com.nestorgarcia.nodocivico.model.ReportStatusHistory

class ReportRepository(
    private val reportDao: ReportDao,
    private val statusHistoryDao: ReportStatusHistoryDao
) {

    fun getAll(): LiveData<List<Report>> = reportDao.getAll()

    fun getById(id: Int): LiveData<Report?> = reportDao.getById(id)

    suspend fun getByIdOnce(id: Int): Report? = reportDao.getByIdOnce(id)

    fun getByStatus(status: ReportStatus): LiveData<List<Report>> =
        reportDao.getByStatus(status)

    fun getPendingCount(): LiveData<Int> = reportDao.getPendingCount()

    fun getSyncedCount(): LiveData<Int> = reportDao.getSyncedCount()

    suspend fun getPending(): List<Report> = reportDao.getPending()

    suspend fun insert(report: Report): Long {
        val id = reportDao.insert(report)
        // Registrar primer estado en el historial
        statusHistoryDao.insert(
            ReportStatusHistory(
                reportId = id.toInt(),
                status = report.status,
                note = "Reporte creado"
            )
        )
        return id
    }

    suspend fun update(report: Report) = reportDao.update(report)

    suspend fun updateStatus(id: Int, status: ReportStatus, note: String? = null) {
        reportDao.updateStatus(id, status)
        statusHistoryDao.insert(
            ReportStatusHistory(
                reportId = id,
                status = status,
                note = note
            )
        )
    }

    suspend fun markSynced(id: Int, remoteId: Int) = reportDao.markSynced(id, remoteId)

    suspend fun deleteById(id: Int) = reportDao.deleteById(id)

    // Historial de estados
    fun getStatusHistory(reportId: Int): LiveData<List<ReportStatusHistory>> =
        statusHistoryDao.getByReport(reportId)
}