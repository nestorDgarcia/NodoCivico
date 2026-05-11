package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.SyncEvent

@Dao
interface SyncEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncEvent: SyncEvent): Long

    @Query("SELECT * FROM sync_events ORDER BY syncedAt DESC")
    fun getAll(): LiveData<List<SyncEvent>>

    @Query("SELECT * FROM sync_events WHERE success = 0 ORDER BY syncedAt DESC")
    fun getFailed(): LiveData<List<SyncEvent>>

    @Query("SELECT COUNT(*) FROM sync_events WHERE success = 0")
    fun getFailedCount(): LiveData<Int>

    @Query("DELETE FROM sync_events")
    suspend fun clearAll()

    @Query("SELECT * FROM sync_events ORDER BY syncedAt DESC LIMIT 50")
    suspend fun getRecentOnce(): List<SyncEvent>
}