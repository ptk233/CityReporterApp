package com.example.cityreporter.dto

import com.example.cityreporter.entity.Priority
import com.example.cityreporter.entity.ReportCategory
import com.example.cityreporter.entity.ReportStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// DTO dla tworzenia nowego zgłoszenia
data class CreateReportRequest(
    @field:NotBlank(message = "Tytuł nie może być pusty")
    @field:Size(max = 200, message = "Tytuł nie może być dłuższy niż 200 znaków")
    val title: String,
    
    @field:NotBlank(message = "Opis nie może być pusty")
    @field:Size(max = 1000, message = "Opis nie może być dłuższy niż 1000 znaków")
    val description: String,
    
    @field:NotNull(message = "Kategoria jest wymagana")
    val category: ReportCategory,
    
    @field:NotNull(message = "Szerokość geograficzna jest wymagana")
    val latitude: Double,
    
    @field:NotNull(message = "Długość geograficzna jest wymagana")
    val longitude: Double,
    
    @field:NotBlank(message = "Adres nie może być pusty")
    val address: String,
    
    val photoUrls: List<String>? = null
)

// DTO dla odpowiedzi ze szczegółami zgłoszenia
data class ReportResponse(
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
    val photoUrls: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val comments: List<CommentResponse> = emptyList()
)

// DTO dla aktualizacji statusu zgłoszenia
data class UpdateReportStatusRequest(
    @field:NotNull(message = "Status jest wymagany")
    val status: ReportStatus,
    
    val comment: String? = null,
    val assignedToId: String? = null
)

// DTO dla komentarza
data class CommentResponse(
    val id: String,
    val userId: String,
    val userName: String,
    val content: String,
    val createdAt: LocalDateTime,
    val isOfficial: Boolean = false
)

// DTO dla filtrowania zgłoszeń
data class ReportFilter(
    val categories: List<ReportCategory>? = null,
    val statuses: List<ReportStatus>? = null,
    val priorities: List<Priority>? = null,
    val userId: String? = null,
    val dateFrom: LocalDateTime? = null,
    val dateTo: LocalDateTime? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusKm: Double? = null,
    val searchText: String? = null
)
