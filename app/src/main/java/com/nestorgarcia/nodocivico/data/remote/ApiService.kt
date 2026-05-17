package com.nestorgarcia.nodocivico.data.remote

import com.nestorgarcia.nodocivico.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/reports")
    suspend fun getReports(): Response<ReportListResponse>

    @GET("api/reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): Response<ReportResponse>

    @POST("api/reports")
    suspend fun createReport(@Body report: ReportRequest): Response<ReportResponse>

    @PUT("api/reports/{id}")
    suspend fun updateReport(
        @Path("id") id: Int,
        @Body update: UpdateStatusRequest
    ): Response<ReportResponse>

    @GET("api/categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @POST("api/users")
    suspend fun registerUser(@Body user: UserRequest): Response<UserResponse>
}