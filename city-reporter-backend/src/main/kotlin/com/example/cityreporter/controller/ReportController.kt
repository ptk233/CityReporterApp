package com.example.cityreporter.controller

import com.example.cityreporter.dto.*
import com.example.cityreporter.entity.ReportStatus
import com.example.cityreporter.service.ReportService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reports")
@CrossOrigin
class ReportController(
    private val reportService: ReportService
) {
    
    @GetMapping
    fun getReports(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) status: ReportStatus?,
        @RequestParam(required = false) userId: String?,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") sortDirection: String
    ): ResponseEntity<Page<ReportResponse>> {
        val sort = Sort.by(
            if (sortDirection == "ASC") Sort.Direction.ASC else Sort.Direction.DESC,
            sortBy
        )
        val pageable = PageRequest.of(page, size, sort)
        
        val filter = ReportFilter(
            statuses = status?.let { listOf(it) },
            userId = userId
        )
        
        val reports = reportService.getReports(filter, pageable)
        return ResponseEntity.ok(reports)
    }
    
    @GetMapping("/{id}")
    fun getReport(@PathVariable id: String): ResponseEntity<ReportResponse> {
        val report = reportService.getReport(id)
        return ResponseEntity.ok(report)
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createReport(
        authentication: Authentication,
        @Valid @RequestBody request: CreateReportRequest
    ): ResponseEntity<ReportResponse> {
        val userId = authentication.principal as String
        val report = reportService.createReport(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(report)
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'TECHNICIAN')")
    fun updateReportStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateReportStatusRequest
    ): ResponseEntity<ReportResponse> {
        val report = reportService.updateReportStatus(id, request)
        return ResponseEntity.ok(report)
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteReport(@PathVariable id: String): ResponseEntity<Map<String, String>> {
        reportService.deleteReport(id)
        return ResponseEntity.ok(mapOf("message" to "Zgłoszenie zostało usunięte"))
    }
    
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    fun getMyReports(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<ReportResponse>> {
        val userId = authentication.principal as String
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val reports = reportService.getUserReports(userId, pageable)
        return ResponseEntity.ok(reports)
    }
    
    @GetMapping("/nearby")
    fun getNearbyReports(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "5.0") radius: Double,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<ReportResponse>> {
        val pageable = PageRequest.of(page, size)
        val filter = ReportFilter(
            latitude = lat,
            longitude = lng,
            radiusKm = radius
        )
        val reports = reportService.getReports(filter, pageable)
        return ResponseEntity.ok(reports)
    }
    
    @GetMapping("/statistics")
    fun getStatistics(): ResponseEntity<Map<String, Any>> {
        // TODO: Implementacja statystyk
        val stats = mapOf(
            "totalReports" to 100,
            "resolvedReports" to 45,
            "pendingReports" to 30,
            "inProgressReports" to 25
        )
        return ResponseEntity.ok(stats)
    }
}
