package com.nestorgarcia.nodocivico.data.remote.dto

data class CategoryResponse(
    val id: Int,
    val name: String,
    val icon: String
)

data class CategoryListResponse(
    val categories: List<CategoryResponse>
)