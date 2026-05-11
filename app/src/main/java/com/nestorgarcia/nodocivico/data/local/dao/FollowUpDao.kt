package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.FollowUp

@Dao
interface FollowUpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(followUp: FollowUp): Long

    @Delete
    suspend fun delete(followUp: FollowUp)

    @Query("SELECT * FROM followups WHERE reportId = :reportId ORDER BY createdAt ASC")
    fun getByReport(reportId: Int): LiveData<List<FollowUp>>

    @Query("SELECT * FROM followups WHERE reportId = :reportId ORDER BY createdAt ASC")
    suspend fun getByReportOnce(reportId: Int): List<FollowUp>

    @Query("SELECT * FROM followups WHERE isSynced = 0")
    suspend fun getPending(): List<FollowUp>

    @Query("UPDATE followups SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)
}