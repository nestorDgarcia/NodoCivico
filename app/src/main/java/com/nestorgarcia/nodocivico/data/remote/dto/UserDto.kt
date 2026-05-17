package com.nestorgarcia.nodocivico.data.remote.dto

data class UserRequest(
    val username: String,
    val zone: String,
    val email: String? = null
)

data class UserResponse(
    val id: Int,
    val username: String,
    val zone: String
)