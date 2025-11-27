package com.example.cityreporter.repository

import com.example.cityreporter.entity.Report
import com.example.cityreporter.entity.ReportCategory
import com.example.cityreporter.entity.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ReportRepository : JpaRepository<Report, String> {

    fun findByStatus(status: ReportStatus, pageable: Pageable): Page<Report>

    fun findByCategory(category: ReportCategory, pageable: Pageable): Page<Report>

    fun findByUserId(userId: String, pageable: Pageable): Page<Report>

    fun countByStatus(status: ReportStatus): Long
}