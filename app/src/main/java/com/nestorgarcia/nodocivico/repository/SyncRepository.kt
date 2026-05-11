package com.nestorgarcia.nodocivico.repository

import androidx.lifecycle.LiveData
import com.nestorgarcia.nodocivico.data.local.dao.ReportDao
import com.nestorgarcia.nodocivico.data.local.dao.SyncEventDao
import com.nestorgarcia.nodocivico.model.SyncEvent

class SyncRepository(
    private val syncEventDao: SyncEventDao,
    private val reportDao: ReportDao
) {

    fun getAll(): LiveData<List<SyncEvent>> = syncEventDao.getAll()

    fun getFailed(): LiveData<List<SyncEvent>> = syncEventDao.getFailed()

    fun getFailedCount(): LiveData<Int> = syncEventDao.getFailedCount()

    fun getPendingCount(): LiveData<Int> = reportDao.getPendingCount()

    fun getSyncedCount(): LiveData<Int> = reportDao.getSyncedCount()

    suspend fun logEvent(
        entityType: String,
        entityId: Int,
        action: String,
        success: Boolean,
        errorMsg: String? = null
    ) {
        syncEventDao.insert(
            SyncEvent(
                entityType = entityType,
                entityId = entityId,
                action = action,
                success = success,
                errorMsg = errorMsg
            )
        )
    }

    suspend fun clearAll() = syncEventDao.clearAll()

    suspend fun getPendingReports() = reportDao.getPending()
}