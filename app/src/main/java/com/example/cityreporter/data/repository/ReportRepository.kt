package com.example.cityreporter.data.repository

import com.example.cityreporter.data.api.CityReporterApi
import com.example.cityreporter.data.models.*
import com.example.cityreporter.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val api: CityReporterApi
) {
    
    suspend fun getReports(
        page: Int = 0,
        status: ReportStatus? = null
    ): Flow<Resource<List<Report>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getReports(
                page = page,
                status = status?.name
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.content))
            } else {
                emit(Resource.Error("Nie można pobrać zgłoszeń"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get reports error")
            emit(Resource.Error(e.message ?: "Błąd pobierania zgłoszeń"))
        }
    }
    
    suspend fun getReport(id: String): Flow<Resource<Report>> = flow {
        Timber.d("ReportRepository: Getting report with ID: $id")
        emit(Resource.Loading())
        try {
            Timber.d("ReportRepository: Calling API for report ID: $id")
            val response = api.getReport(id)
            Timber.d("ReportRepository: API response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            if (response.isSuccessful && response.body() != null) {
                Timber.d("ReportRepository: Successfully got report: ${response.body()!!.id}")
                emit(Resource.Success(response.body()!!))
            } else {
                Timber.e("ReportRepository: Report not found, response code: ${response.code()}")
                emit(Resource.Error("Zgłoszenie nie znalezione"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get report error for ID: $id")
            emit(Resource.Error(e.message ?: "Błąd pobierania zgłoszenia"))
        }
    }
    
    suspend fun createReport(request: CreateReportRequest): Flow<Resource<Report>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createReport(request)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Nie można utworzyć zgłoszenia"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Create report error")
            emit(Resource.Error(e.message ?: "Błąd tworzenia zgłoszenia"))
        }
    }
    
    suspend fun getMyReports(page: Int = 0): Flow<Resource<List<Report>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMyReports(page = page)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.content))
            } else {
                emit(Resource.Error("Nie można pobrać Twoich zgłoszeń"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get my reports error")
            emit(Resource.Error(e.message ?: "Błąd pobierania zgłoszeń"))
        }
    }
    
    suspend fun getNearbyReports(
        latitude: Double,
        longitude: Double,
        radius: Double = 5.0
    ): Flow<Resource<List<Report>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getNearbyReports(latitude, longitude, radius)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.content))
            } else {
                emit(Resource.Error("Nie można pobrać zgłoszeń w pobliżu"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Get nearby reports error")
            emit(Resource.Error(e.message ?: "Błąd pobierania zgłoszeń"))
        }
    }
    
    suspend fun updateReportStatus(
        id: String,
        status: ReportStatus,
        comment: String? = null
    ): Flow<Resource<Report>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateReportStatus(
                id = id,
                request = UpdateReportStatusRequest(status, comment)
            )
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Nie można zaktualizować statusu"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Update status error")
            emit(Resource.Error(e.message ?: "Błąd aktualizacji statusu"))
        }
    }
    
    suspend fun deleteReport(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteReport(id)
            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Nie można usunąć zgłoszenia"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Delete report error")
            emit(Resource.Error(e.message ?: "Błąd usuwania zgłoszenia"))
        }
    }
}