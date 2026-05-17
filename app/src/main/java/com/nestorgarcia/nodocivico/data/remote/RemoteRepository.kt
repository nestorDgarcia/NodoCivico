package com.nestorgarcia.nodocivico.repository

import android.content.Context
import com.nestorgarcia.nodocivico.data.local.dao.CategoryDao
import com.nestorgarcia.nodocivico.data.local.dao.ReportDao
import com.nestorgarcia.nodocivico.data.local.dao.SyncEventDao
import com.nestorgarcia.nodocivico.data.remote.RetrofitClient
import com.nestorgarcia.nodocivico.data.remote.dto.ReportRequest
import com.nestorgarcia.nodocivico.data.remote.dto.UpdateStatusRequest
import com.nestorgarcia.nodocivico.data.remote.dto.UserRequest
import com.nestorgarcia.nodocivico.model.Category
import com.nestorgarcia.nodocivico.model.SyncEvent

class RemoteRepository(
    private val context: Context,
    private val reportDao: ReportDao,
    private val categoryDao: CategoryDao,
    private val syncEventDao: SyncEventDao
) {

    private val api get() = RetrofitClient.getApi(context)

    // ── Sincronizar reportes pendientes ──────────────────────
    suspend fun syncPendingReports(): SyncResult {
        val pending = reportDao.getPending()
        var successCount = 0
        var errorCount = 0

        pending.forEach { report ->
            try {
                val request = ReportRequest(
                    title = report.title,
                    description = report.description,
                    category_id = report.categoryId,
                    user_id = report.userId,
                    priority = report.priority.name,
                    location = report.location,
                    status = report.status.name
                )

                val response = if (report.remoteId == null) {
                    api.createReport(request)
                } else {
                    api.updateReport(
                        report.remoteId,
                        UpdateStatusRequest(status = report.status.name)
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    val remoteId = response.body()!!.id
                    reportDao.markSynced(report.id, remoteId)
                    logEvent("report", report.id, "SYNC", true)
                    successCount++
                } else {
                    logEvent("report", report.id, "SYNC", false, "HTTP ${response.code()}")
                    errorCount++
                }
            } catch (e: Exception) {
                logEvent("report", report.id, "SYNC", false, e.message)
                errorCount++
            }
        }

        return SyncResult(successCount, errorCount)
    }

    // ── Obtener categorías del servidor ──────────────────────
    suspend fun fetchCategories(): Boolean {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val categories = response.body()!!.categories.map {
                    Category(
                        name = it.name,
                        iconCode = it.icon,
                        remoteId = it.id
                    )
                }
                categoryDao.insertAll(categories)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    // ── Registrar usuario en el servidor ─────────────────────
    suspend fun registerUserRemote(
        username: String,
        zone: String,
        email: String
    ): Int? {
        return try {
            val response = api.registerUser(
                UserRequest(username = username, zone = zone, email = email)
            )
            if (response.isSuccessful) response.body()?.id else null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun logEvent(
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

    data class SyncResult(val success: Int, val errors: Int)
}