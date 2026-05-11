package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconCode: String = "",
    val colorHex: String = "#2563EB",
    val remoteId: Int? = null
)