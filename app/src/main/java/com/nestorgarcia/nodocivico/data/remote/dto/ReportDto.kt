package com.nestorgarcia.nodocivico.data.remote.dto

data class ReportRequest(
    val title: String,
    val description: String,
    val category_id: Int?,
    val user_id: Int,
    val priority: String,
    val location: String,
    val status: String
)

data class ReportResponse(
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val category: String?,
    val location: String,
    val is_synced: Boolean,
    val created_at: String
)

data class ReportListResponse(
    val reports: List<ReportResponse>,
    val total: Int
)

data class UpdateStatusRequest(
    val status: String,
    val note: String? = null
)