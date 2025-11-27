package com.example.cityreporter.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reports")
data class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = "",

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, length = 1000)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ReportCategory,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ReportStatus = ReportStatus.NEW,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    val address: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "report_photos", joinColumns = [JoinColumn(name = "report_id")])
    @Column(name = "photo_url", columnDefinition = "TEXT")
    val photoUrls: List<String> = emptyList(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priority: Priority = Priority.NORMAL,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ReportCategory {
    ROAD_DAMAGE, LIGHTING, TRAFFIC_SIGNS, ILLEGAL_DUMPING,
    VANDALISM, GREEN_AREAS, SIDEWALK, PLAYGROUND,
    PUBLIC_TRANSPORT, OTHER
}

enum class ReportStatus {
    NEW, IN_REVIEW, ACCEPTED, IN_PROGRESS, RESOLVED, REJECTED, DUPLICATE
}

enum class Priority {
    LOW, NORMAL, HIGH, CRITICAL
}