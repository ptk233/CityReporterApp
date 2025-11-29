package com.example.cityreporter.dto

import com.example.cityreporter.entity.Report
import com.example.cityreporter.entity.User
import com.example.cityreporter.entity.UserRole
import java.time.LocalDateTime

/**
 * DTO dla użytkownika (używane w AdminController)
 */
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val role: UserRole,
    val isActive: Boolean,
    val points: Int,
    val createdAt: LocalDateTime
)

/**
 * Konwersja Report Entity → ReportDto
 */
fun Report.toDto(): ReportDto {
    return ReportDto(
        id = this.id,
        userId = this.user.id,
        userName = this.user.name,
        userEmail = this.user.email,
        title = this.title,
        description = this.description,
        category = this.category,
        status = this.status,
        priority = this.priority,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        photoUrls = this.photoUrls,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Konwersja User Entity → UserDto
 */
fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        role = this.role,
        isActive = this.isActive,
        points = this.points,
        createdAt = this.createdAt
    )
}

/**
 * DTO dla zgłoszenia (używane w AdminController)
 */
data class ReportDto(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val title: String,
    val description: String,
    val category: com.example.cityreporter.entity.ReportCategory,
    val status: com.example.cityreporter.entity.ReportStatus,
    val priority: com.example.cityreporter.entity.Priority,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val photoUrls: List<String>,
    val createdAt: java.time.LocalDateTime,
    val updatedAt: java.time.LocalDateTime
)
