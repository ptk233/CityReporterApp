package com.example.cityreporter.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityreporter.data.models.CreateReportRequest
import com.example.cityreporter.data.models.Report
import com.example.cityreporter.data.models.ReportCategory
import com.example.cityreporter.data.models.ReportStatus
import com.example.cityreporter.data.repository.ReportRepository
import com.example.cityreporter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {
    
    private val _reportsState = MutableStateFlow<Resource<List<Report>>>(Resource.Loading())
    val reportsState: StateFlow<Resource<List<Report>>> = _reportsState.asStateFlow()
    
    private val _myReportsState = MutableStateFlow<Resource<List<Report>>>(Resource.Loading())
    val myReportsState: StateFlow<Resource<List<Report>>> = _myReportsState.asStateFlow()
    
    private val _reportDetailState = MutableStateFlow<Resource<Report>?>(null)
    val reportDetailState: StateFlow<Resource<Report>?> = _reportDetailState.asStateFlow()
    
    private val _createReportState = MutableStateFlow<Resource<Report>?>(null)
    val createReportState: StateFlow<Resource<Report>?> = _createReportState.asStateFlow()
    
    private val _nearbyReportsState = MutableStateFlow<Resource<List<Report>>>(Resource.Loading())
    val nearbyReportsState: StateFlow<Resource<List<Report>>> = _nearbyReportsState.asStateFlow()
    
    fun loadReports(page: Int = 0, status: ReportStatus? = null) {
        viewModelScope.launch {
            reportRepository.getReports(page, status).collect { result ->
                _reportsState.value = result
            }
        }
    }
    
    fun loadMyReports(page: Int = 0) {
        viewModelScope.launch {
            reportRepository.getMyReports(page).collect { result ->
                _myReportsState.value = result
            }
        }
    }
    
    fun loadReportDetail(reportId: String) {
        viewModelScope.launch {
            timber.log.Timber.d("ReportViewModel: Loading report detail for ID: $reportId")
            reportRepository.getReport(reportId).collect { result ->
                timber.log.Timber.d("ReportViewModel: Report detail result: $result")
                _reportDetailState.value = result
            }
        }
    }
    
    fun createReport(
        title: String,
        description: String,
        category: ReportCategory,
        latitude: Double,
        longitude: Double,
        address: String,
        photoUrls: List<String>? = null
    ) {
        viewModelScope.launch {
            val request = CreateReportRequest(
                title = title,
                description = description,
                category = category,
                latitude = latitude,
                longitude = longitude,
                address = address,
                photoUrls = photoUrls
            )
            reportRepository.createReport(request).collect { result ->
                _createReportState.value = result
            }
        }
    }
    
    fun loadNearbyReports(latitude: Double, longitude: Double, radius: Double = 5.0) {
        viewModelScope.launch {
            reportRepository.getNearbyReports(latitude, longitude, radius).collect { result ->
                _nearbyReportsState.value = result
            }
        }
    }
    
    fun updateReportStatus(reportId: String, status: ReportStatus, comment: String? = null) {
        viewModelScope.launch {
            reportRepository.updateReportStatus(reportId, status, comment).collect { result ->
                if (result is Resource.Success) {
                    // Reload the report detail
                    loadReportDetail(reportId)
                }
            }
        }
    }
    
    fun clearCreateReportState() {
        _createReportState.value = null
    }
}