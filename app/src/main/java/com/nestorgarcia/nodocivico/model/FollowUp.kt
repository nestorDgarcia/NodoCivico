package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "followups",
    foreignKeys = [
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reportId"), Index("userId")]
)
data class FollowUp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val reportId: Int,
    val userId: Int,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)