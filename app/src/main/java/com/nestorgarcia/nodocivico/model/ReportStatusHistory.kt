package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "report_status_history",
    foreignKeys = [
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reportId")]
)
data class ReportStatusHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val reportId: Int,
    val status: ReportStatus,
    val note: String? = null,
    val changedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)