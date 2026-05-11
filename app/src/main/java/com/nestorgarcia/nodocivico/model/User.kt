package com.nestorgarcia.nodocivico.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,   // se guardará hasheada
    val zone: String,
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val remoteId: Int? = null
)