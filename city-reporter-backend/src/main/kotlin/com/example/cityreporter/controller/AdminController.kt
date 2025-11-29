package com.example.cityreporter.controller

import com.example.cityreporter.dto.*
import com.example.cityreporter.entity.ReportStatus
import com.example.cityreporter.entity.User
import com.example.cityreporter.entity.UserRole
import com.example.cityreporter.repository.ReportRepository
import com.example.cityreporter.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) {

    // ==================== ZARZĄDZANIE ZGŁOSZENIAMI ====================

    /**
     * Pobierz wszystkie zgłoszenia (z paginacją)
     */
    @GetMapping("/reports")
    fun getAllReports(pageable: Pageable): ResponseEntity<Page<ReportDto>> {
        val reports = reportRepository.findAll(pageable)
        return ResponseEntity.ok(reports.map { it.toDto() })
    }

    /**
     * Pobierz zgłoszenia według statusu
     */
    @GetMapping("/reports/status/{status}")
    fun getReportsByStatus(
        @PathVariable status: ReportStatus,
        pageable: Pageable
    ): ResponseEntity<Page<ReportDto>> {
        val reports = reportRepository.findByStatus(status, pageable)
        return ResponseEntity.ok(reports.map { it.toDto() })
    }

    /**
     * Zmień status zgłoszenia
     */
    @PutMapping("/reports/{id}/status")
    fun updateReportStatus(
        @PathVariable id: String,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<ReportDto> {
        val report = reportRepository.findById(id)
            .orElseThrow { RuntimeException("Report not found") }

        val updatedReport = report.copy(
            status = request.status,
            updatedAt = LocalDateTime.now()
        )
        
        reportRepository.save(updatedReport)
        return ResponseEntity.ok(updatedReport.toDto())
    }

    /**
     * Usuń zgłoszenie
     */
    @DeleteMapping("/reports/{id}")
    fun deleteReport(@PathVariable id: String): ResponseEntity<MessageResponse> {
        if (!reportRepository.existsById(id)) {
            throw RuntimeException("Report not found")
        }
        
        reportRepository.deleteById(id)
        return ResponseEntity.ok(MessageResponse("Report deleted successfully"))
    }

    // ==================== ZARZĄDZANIE UŻYTKOWNIKAMI ====================

    /**
     * Pobierz wszystkich użytkowników
     */
    @GetMapping("/users")
    fun getAllUsers(pageable: Pageable): ResponseEntity<Page<UserDto>> {
        val users = userRepository.findAll(pageable)
        return ResponseEntity.ok(users.map { it.toDto() })
    }

    /**
     * Pobierz użytkownika po ID
     */
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserDto> {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("User not found") }
        return ResponseEntity.ok(user.toDto())
    }

    /**
     * Zablokuj/odblokuj użytkownika
     */
    @PutMapping("/users/{id}/toggle-active")
    fun toggleUserActive(@PathVariable id: String): ResponseEntity<UserDto> {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("User not found") }

        val updatedUser = user.copy(isActive = !user.isActive)
        userRepository.save(updatedUser)
        
        return ResponseEntity.ok(updatedUser.toDto())
    }

    /**
     * Zmień rolę użytkownika
     */
    @PutMapping("/users/{id}/role")
    fun updateUserRole(
        @PathVariable id: String,
        @RequestBody request: UpdateRoleRequest
    ): ResponseEntity<UserDto> {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("User not found") }

        val updatedUser = user.copy(role = request.role)
        userRepository.save(updatedUser)
        
        return ResponseEntity.ok(updatedUser.toDto())
    }

    // ==================== STATYSTYKI ====================

    /**
     * Pobierz statystyki dashboardu
     */
    @GetMapping("/dashboard/stats")
    fun getDashboardStats(): ResponseEntity<DashboardStats> {
        val totalReports = reportRepository.count()
        val totalUsers = userRepository.count()
        
        val reportsByStatus = ReportStatus.values().associate { status ->
            status.name to reportRepository.countByStatus(status)
        }

        val recentReports = reportRepository.findTop10ByOrderByCreatedAtDesc()
            .map { it.toDto() }

        val stats = DashboardStats(
            totalReports = totalReports,
            totalUsers = totalUsers,
            reportsByStatus = reportsByStatus,
            recentReports = recentReports
        )

        return ResponseEntity.ok(stats)
    }
}

// ==================== DTOs ====================

data class UpdateStatusRequest(
    val status: ReportStatus
)

data class UpdateRoleRequest(
    val role: UserRole
)

data class MessageResponse(
    val message: String
)

data class DashboardStats(
    val totalReports: Long,
    val totalUsers: Long,
    val reportsByStatus: Map<String, Long>,
    val recentReports: List<ReportDto>
)
