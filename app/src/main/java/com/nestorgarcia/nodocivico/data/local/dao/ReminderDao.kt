package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.Reminder

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY triggerAt ASC")
    fun getAll(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE reportId = :reportId ORDER BY triggerAt ASC")
    fun getByReport(reportId: Int): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isTriggered = 0 AND triggerAt > :now ORDER BY triggerAt ASC")
    suspend fun getPendingReminders(now: Long = System.currentTimeMillis()): List<Reminder>

    @Query("UPDATE reminders SET isTriggered = 1 WHERE id = :id")
    suspend fun markTriggered(id: Int)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Int): Reminder?
}