package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.ReportStatusHistory

@Dao
interface ReportStatusHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statusHistory: ReportStatusHistory): Long

    @Delete
    suspend fun delete(statusHistory: ReportStatusHistory)

    @Query("SELECT * FROM report_status_history WHERE reportId = :reportId ORDER BY changedAt DESC")
    fun getByReport(reportId: Int): LiveData<List<ReportStatusHistory>>

    @Query("SELECT * FROM report_status_history WHERE reportId = :reportId ORDER BY changedAt DESC")
    suspend fun getByReportOnce(reportId: Int): List<ReportStatusHistory>

    @Query("SELECT * FROM report_status_history WHERE isSynced = 0")
    suspend fun getPending(): List<ReportStatusHistory>

    @Query("UPDATE report_status_history SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)
}