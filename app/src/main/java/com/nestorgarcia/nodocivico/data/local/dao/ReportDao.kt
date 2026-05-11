package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.Report
import com.nestorgarcia.nodocivico.model.ReportStatus

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: Report): Long

    @Update
    suspend fun update(report: Report)

    @Delete
    suspend fun delete(report: Report)

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<Report>>

    @Query("SELECT * FROM reports WHERE id = :id")
    fun getById(id: Int): LiveData<Report?>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getByIdOnce(id: Int): Report?

    @Query("SELECT * FROM reports WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: ReportStatus): LiveData<List<Report>>

    @Query("SELECT * FROM reports WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getPending(): List<Report>

    @Query("UPDATE reports SET isSynced = 1, remoteId = :remoteId WHERE id = :id")
    suspend fun markSynced(id: Int, remoteId: Int)

    @Query("UPDATE reports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: ReportStatus)

    @Query("SELECT COUNT(*) FROM reports WHERE isSynced = 0")
    fun getPendingCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM reports WHERE isSynced = 1")
    fun getSyncedCount(): LiveData<Int>

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteById(id: Int)
}