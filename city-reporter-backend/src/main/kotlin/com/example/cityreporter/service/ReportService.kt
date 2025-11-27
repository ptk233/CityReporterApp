package com.example.cityreporter.service

import com.example.cityreporter.dto.*
import com.example.cityreporter.entity.Report
import com.example.cityreporter.entity.ReportStatus
import com.example.cityreporter.entity.User
import com.example.cityreporter.repository.ReportRepository
import com.example.cityreporter.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ReportService(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) {
    
    fun createReport(userId: String, request: CreateReportRequest): ReportResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("Użytkownik nie znaleziony") }
        
        val report = Report(
            user = user,
            title = request.title,
            description = request.description,
            category = request.category,
            latitude = request.latitude,
            longitude = request.longitude,
            address = request.address,
            photoUrls = request.photoUrls ?: emptyList()
        )
        
        val savedReport = reportRepository.save(report)
        
        // Dodaj punkty użytkownikowi za zgłoszenie
        updateUserPoints(user, 10)
        
        return toReportResponse(savedReport)
    }
    
    fun getReport(id: String): ReportResponse {
        val report = reportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Zgłoszenie nie znalezione") }
        return toReportResponse(report)
    }
    
    fun getReports(filter: ReportFilter, pageable: Pageable): Page<ReportResponse> {
        // Tutaj można dodać bardziej zaawansowane filtrowanie
        val reports = if (filter.userId != null) {
            reportRepository.findByUserId(filter.userId, pageable)
        } else {
            reportRepository.findAll(pageable)
        }
        
        return reports.map { toReportResponse(it) }
    }
    
    fun updateReportStatus(id: String, request: UpdateReportStatusRequest): ReportResponse {
        val report = reportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Zgłoszenie nie znalezione") }
        
        // Utworzenie nowej instancji z zaktualizowanym statusem
        val updatedReport = report.copy(
            status = request.status,
            updatedAt = LocalDateTime.now()
        )
        
        val savedReport = reportRepository.save(updatedReport)
        
        // Jeśli zgłoszenie zostało rozwiązane, dodaj punkty użytkownikowi
        if (request.status == ReportStatus.RESOLVED) {
            updateUserPoints(savedReport.user, 50)
        }
        
        return toReportResponse(savedReport)
    }
    
    fun deleteReport(id: String) {
        if (!reportRepository.existsById(id)) {
            throw EntityNotFoundException("Zgłoszenie nie znalezione")
        }
        reportRepository.deleteById(id)
    }
    
    fun getUserReports(userId: String, pageable: Pageable): Page<ReportResponse> {
        return reportRepository.findByUserId(userId, pageable)
            .map { toReportResponse(it) }
    }
    
    fun getReportsByStatus(status: ReportStatus, pageable: Pageable): Page<ReportResponse> {
        return reportRepository.findByStatus(status, pageable)
            .map { toReportResponse(it) }
    }
    
    private fun updateUserPoints(user: User, points: Int) {
        val updatedUser = user.copy(points = user.points + points)
        userRepository.save(updatedUser)
    }
    
    private fun toReportResponse(report: Report): ReportResponse {
        return ReportResponse(
            id = report.id,
            userId = report.user.id,
            userName = report.user.name,
            title = report.title,
            description = report.description,
            category = report.category,
            status = report.status,
            priority = report.priority,
            latitude = report.latitude,
            longitude = report.longitude,
            address = report.address,
            photoUrls = report.photoUrls,
            createdAt = report.createdAt,
            updatedAt = report.updatedAt,
            comments = emptyList() // TODO: Dodać obsługę komentarzy
        )
    }
}
