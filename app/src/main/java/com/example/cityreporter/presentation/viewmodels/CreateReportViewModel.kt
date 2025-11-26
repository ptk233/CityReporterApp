package com.example.cityreporter.presentation.viewmodels

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityreporter.data.models.ReportCategory
import com.example.cityreporter.data.models.CreateReportRequest
import com.example.cityreporter.data.repository.ReportRepository
import com.example.cityreporter.utils.LocationHelper
import com.example.cityreporter.utils.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

data class CreateReportUiState(
    val title: String = "",
    val description: String = "",
    val category: ReportCategory = ReportCategory.OTHER,
    val location: LatLng? = null,
    val address: String = "",
    val photos: List<Uri> = emptyList(),
    val isLocationLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val createdReportId: String? = null,
    val error: String? = null
)

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val locationHelper: LocationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, error = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description, error = null) }
    }

    fun updateCategory(category: ReportCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateAddress(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun addPhoto(uri: Uri) {
        _uiState.update { currentState ->
            if (currentState.photos.size < 5) {
                currentState.copy(photos = currentState.photos + uri, error = null)
            } else {
                currentState.copy(error = "Maksymalna liczba zdjęć to 5")
            }
        }
    }

    fun removePhoto(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(photos = currentState.photos - uri)
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true, error = null) }
            
            try {
                locationHelper.getCurrentLocation { location ->
                    location?.let { loc ->
                        val latLng = LatLng(loc.latitude, loc.longitude)
                        _uiState.update { 
                            it.copy(
                                location = latLng,
                                isLocationLoading = false
                            )
                        }
                        // Pobierz adres z koordynatów
                        getAddressFromLocation(loc.latitude, loc.longitude)
                    } ?: run {
                        _uiState.update { 
                            it.copy(
                                isLocationLoading = false,
                                error = "Nie można pobrać lokalizacji"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error getting location")
                _uiState.update { 
                    it.copy(
                        isLocationLoading = false,
                        error = "Błąd pobierania lokalizacji: ${e.message}"
                    )
                }
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Nowa API dla Android 13+
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val addressText = buildString {
                                address.thoroughfare?.let { append(it) }
                                address.subThoroughfare?.let { append(" $it") }
                                if (isNotEmpty()) append(", ")
                                address.locality?.let { append(it) }
                            }
                            
                            _uiState.update { 
                                it.copy(address = addressText.ifEmpty { "Nieznany adres" })
                            }
                        } else {
                            _uiState.update { it.copy(address = "Nieznany adres") }
                        }
                    }
                } else {
                    // Stara API dla Android < 13
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val addressText = buildString {
                            address.thoroughfare?.let { append(it) }
                            address.subThoroughfare?.let { append(" $it") }
                            if (isNotEmpty()) append(", ")
                            address.locality?.let { append(it) }
                        }
                        
                        _uiState.update { 
                            it.copy(address = addressText.ifEmpty { "Nieznany adres" })
                        }
                    } else {
                        _uiState.update { it.copy(address = "Nieznany adres") }
                    }
                }
            } catch (e: IOException) {
                Timber.e(e, "Error getting address from location")
                _uiState.update { it.copy(address = "Nieznany adres") }
            }
        }
    }

    fun submitReport() {
        // Walidacja
        val currentState = _uiState.value
        
        when {
            currentState.title.isBlank() -> {
                _uiState.update { it.copy(error = "Tytuł jest wymagany") }
                return
            }
            currentState.description.isBlank() -> {
                _uiState.update { it.copy(error = "Opis jest wymagany") }
                return
            }
            currentState.location == null -> {
                _uiState.update { it.copy(error = "Lokalizacja jest wymagana") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            
            try {
                // Konwertuj zdjęcia na base64
                val photoUrls = convertPhotosToBase64()
                
                // Wywołaj ReportViewModel do utworzenia zgłoszenia
                val location = currentState.location!!
                
                reportRepository.createReport(
                    CreateReportRequest(
                        title = currentState.title,
                        description = currentState.description,
                        category = currentState.category,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = currentState.address.ifBlank { "Nieznany adres" },
                        photoUrls = photoUrls.ifEmpty { null }
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { 
                                it.copy(
                                    isSubmitting = false,
                                    submitSuccess = true,
                                    createdReportId = result.data?.id
                                )
                            }
                            Timber.d("Report created successfully: ${result.data?.id}")
                        }
                        is Resource.Error -> {
                            _uiState.update { 
                                it.copy(
                                    isSubmitting = false,
                                    error = result.message ?: "Nie można utworzyć zgłoszenia"
                                )
                            }
                            Timber.e("Error creating report: ${result.message}")
                        }
                        is Resource.Loading -> {
                            // Already handling loading state
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error submitting report")
                _uiState.update { 
                    it.copy(
                        isSubmitting = false,
                        error = "Błąd wysyłania zgłoszenia: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun convertPhotosToBase64(): List<String> {
        val base64Photos = mutableListOf<String>()
        
        try {
            _uiState.value.photos.forEach { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bytes = stream.readBytes()
                    val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    // Dodaj prefix data URL dla obrazów JPEG
                    base64Photos.add("data:image/jpeg;base64,$base64String")
                    Timber.d("Converted photo to base64, size: ${base64String.length}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error converting photos to base64")
        }
        
        return base64Photos
    }

    fun reset() {
        _uiState.value = CreateReportUiState()
    }
}
