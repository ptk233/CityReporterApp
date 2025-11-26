package com.example.cityreporter.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

// User related models
@Parcelize
data class User(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val role: UserRole,
    val points: Int = 0,
    val isActive: Boolean = true
) : Parcelable

enum class UserRole {
    CITIZEN, MODERATOR, ADMIN, TECHNICIAN
}

// Report related models
@Parcelize
data class Report(
    val id: String,
    val userId: String,
    val userName: String,
    val title: String,
    val description: String,
    val category: ReportCategory,
    val status: ReportStatus,
    val priority: Priority,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val photoUrls: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val comments: List<Comment> = emptyList()
) : Parcelable

@Parcelize
data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val content: String,
    val createdAt: String,
    val isOfficial: Boolean = false
) : Parcelable

enum class ReportCategory {
    ROAD_DAMAGE,
    LIGHTING,
    TRAFFIC_SIGNS,
    ILLEGAL_DUMPING,
    VANDALISM,
    GREEN_AREAS,
    SIDEWALK,
    PLAYGROUND,
    PUBLIC_TRANSPORT,
    OTHER;
    
    fun getDisplayName(): String = when(this) {
        ROAD_DAMAGE -> "Uszkodzenie nawierzchni"
        LIGHTING -> "Oświetlenie uliczne"
        TRAFFIC_SIGNS -> "Znaki drogowe"
        ILLEGAL_DUMPING -> "Nielegalne wysypisko"
        VANDALISM -> "Wandalizm"
        GREEN_AREAS -> "Tereny zielone"
        SIDEWALK -> "Chodnik"
        PLAYGROUND -> "Plac zabaw"
        PUBLIC_TRANSPORT -> "Transport publiczny"
        OTHER -> "Inne"
    }
}

enum class ReportStatus {
    NEW,
    IN_REVIEW,
    ACCEPTED,
    IN_PROGRESS,
    RESOLVED,
    REJECTED,
    DUPLICATE;
    
    fun getDisplayName(): String = when(this) {
        NEW -> "Nowe"
        IN_REVIEW -> "W trakcie weryfikacji"
        ACCEPTED -> "Przyjęte do realizacji"
        IN_PROGRESS -> "W trakcie naprawy"
        RESOLVED -> "Rozwiązane"
        REJECTED -> "Odrzucone"
        DUPLICATE -> "Duplikat"
    }
    
    fun getColor(): Long = when(this) {
        NEW -> 0xFF2196F3
        IN_REVIEW -> 0xFFFF9800
        ACCEPTED -> 0xFF4CAF50
        IN_PROGRESS -> 0xFFFFC107
        RESOLVED -> 0xFF8BC34A
        REJECTED -> 0xFFF44336
        DUPLICATE -> 0xFF9E9E9E
    }
}

enum class Priority {
    LOW, NORMAL, HIGH, CRITICAL;
    
    fun getDisplayName(): String = when(this) {
        LOW -> "Niski"
        NORMAL -> "Normalny"
        HIGH -> "Wysoki"
        CRITICAL -> "Krytyczny"
    }
    
    fun getColor(): Long = when(this) {
        LOW -> 0xFF4CAF50
        NORMAL -> 0xFF2196F3
        HIGH -> 0xFFFF9800
        CRITICAL -> 0xFFF44336
    }
}

// Request/Response DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String? = null
)

data class CreateReportRequest(
    val title: String,
    val description: String,
    val category: ReportCategory,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val photoUrls: List<String>? = null
)

data class UpdateReportStatusRequest(
    val status: ReportStatus,
    val comment: String? = null,
    val assignedToId: String? = null
)

// Generic API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)