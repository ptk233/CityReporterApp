package com.example.cityreporter.presentation.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityreporter.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationHelper: LocationHelper
) : ViewModel() {
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    private val _isLocationLoading = MutableStateFlow(false)
    val isLocationLoading: StateFlow<Boolean> = _isLocationLoading.asStateFlow()
    
    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()
    
    init {
        // Try to get current location on init
        getCurrentLocation()
    }
    
    fun getCurrentLocation() {
        viewModelScope.launch {
            _isLocationLoading.value = true
            _locationError.value = null
            
            try {
                locationHelper.getCurrentLocation { location ->
                    if (location != null) {
                        _currentLocation.value = location
                        Timber.d("Current location: ${location.latitude}, ${location.longitude}")
                    } else {
                        _locationError.value = "Nie można pobrać lokalizacji"
                        Timber.w("Could not get current location")
                    }
                    _isLocationLoading.value = false
                }
            } catch (e: Exception) {
                _locationError.value = "Błąd pobierania lokalizacji: ${e.message}"
                Timber.e(e, "Error getting current location")
                _isLocationLoading.value = false
            }
        }
    }
    
    fun refreshLocation() {
        getCurrentLocation()
    }
    
    fun hasLocationPermission(): Boolean {
        return locationHelper.hasLocationPermission()
    }
    
    suspend fun isLocationEnabled(): Boolean {
        return locationHelper.isLocationEnabled()
    }
}