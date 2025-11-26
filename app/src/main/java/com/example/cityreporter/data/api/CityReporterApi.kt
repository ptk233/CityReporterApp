package com.example.cityreporter.data.api

import com.example.cityreporter.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface CityReporterApi {
    
    // Authentication endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): Response<LoginResponse>
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<User>
    
    @PUT("api/auth/me")
    suspend fun updateProfile(@Body request: Map<String, String>): Response<User>
    
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): Response<Map<String, String>>
    
    // Report endpoints
    @GET("api/reports")
    suspend fun getReports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("status") status: String? = null,
        @Query("userId") userId: String? = null,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("sortDirection") sortDirection: String = "DESC"
    ): Response<ReportsResponse>
    
    @GET("api/reports/{id}")
    suspend fun getReport(@Path("id") id: String): Response<Report>
    
    @POST("api/reports")
    suspend fun createReport(@Body request: CreateReportRequest): Response<Report>
    
    @PUT("api/reports/{id}/status")
    suspend fun updateReportStatus(
        @Path("id") id: String,
        @Body request: UpdateReportStatusRequest
    ): Response<Report>
    
    @DELETE("api/reports/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Map<String, String>>
    
    @GET("api/reports/my")
    suspend fun getMyReports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ReportsResponse>
    
    @GET("api/reports/nearby")
    suspend fun getNearbyReports(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 5.0,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ReportsResponse>
    
    @GET("api/reports/statistics")
    suspend fun getStatistics(): Response<Map<String, Any>>
}

// Response wrapper for paginated results
data class ReportsResponse(
    val content: List<Report>,
    val pageable: Pageable,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val first: Boolean,
    val numberOfElements: Int
)

data class Pageable(
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)

data class Sort(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)