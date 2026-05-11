package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_events")
data class SyncEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entityType: String,   // "report", "status", "followup"
    val entityId: Int,
    val action: String,       // "CREATE", "UPDATE"
    val success: Boolean = false,
    val syncedAt: Long = System.currentTimeMillis(),
    val errorMsg: String? = null
)