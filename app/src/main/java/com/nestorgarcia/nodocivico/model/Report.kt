package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Priority { LOW, MEDIUM, HIGH }
enum class ReportStatus { OPEN, IN_PROGRESS, CLOSED }

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("userId")]
)
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val categoryId: Int?,
    val userId: Int,
    val priority: Priority = Priority.MEDIUM,
    val status: ReportStatus = ReportStatus.OPEN,
    val location: String = "",
    val evidencePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val remoteId: Int? = null
)