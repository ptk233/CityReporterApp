package com.example.cityreporter.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationHelper @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Pobiera aktualną lokalizację użytkownika
     * @param onResult callback z lokalizacją lub null jeśli nie udało się pobrać
     */
    fun getCurrentLocation(onResult: (Location?) -> Unit) {
        // Sprawdzenie uprawnień
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.w("Location permissions not granted")
            onResult(null)
            return
        }

        try {
            // Najpierw spróbuj pobrać ostatnią znaną lokalizację
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Timber.d("Got last known location: ${location.latitude}, ${location.longitude}")
                        onResult(location)
                    } else {
                        // Jeśli nie ma ostatniej lokalizacji, poproś o nową
                        requestNewLocation(onResult)
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed to get last location")
                    // Spróbuj pobrać nową lokalizację
                    requestNewLocation(onResult)
                }
        } catch (e: SecurityException) {
            Timber.e(e, "Security exception when getting location")
            onResult(null)
        }
    }

    /**
     * Żąda nowej lokalizacji od systemu
     */
    private fun requestNewLocation(onResult: (Location?) -> Unit) {
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            // Użyj getCurrentLocation dla jednorazowego pobrania lokalizacji
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    Timber.d("Got current location: ${location.latitude}, ${location.longitude}")
                    onResult(location)
                } else {
                    // Jako ostatnia deska ratunku, użyj requestLocationUpdates
                    requestLocationWithUpdates(onResult)
                }
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get current location")
                // Spróbuj z requestLocationUpdates
                requestLocationWithUpdates(onResult)
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Security exception when requesting new location")
            onResult(null)
        }
    }

    /**
     * Żąda aktualizacji lokalizacji (jako ostatnia opcja)
     */
    private fun requestLocationWithUpdates(onResult: (Location?) -> Unit) {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000 // 5 sekund
            ).apply {
                setMinUpdateIntervalMillis(2000) // Minimum 2 sekundy między aktualizacjami
                setMaxUpdateDelayMillis(10000) // Maksymalnie 10 sekund opóźnienia
                setWaitForAccurateLocation(false) // Nie czekaj na bardzo dokładną lokalizację
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        Timber.d("Got location from updates: ${location.latitude}, ${location.longitude}")
                        // Zatrzymaj aktualizacje po otrzymaniu pierwszej lokalizacji
                        fusedLocationClient.removeLocationUpdates(this)
                        onResult(location)
                    } ?: run {
                        Timber.w("Location result is null")
                        fusedLocationClient.removeLocationUpdates(this)
                        onResult(null)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Ustaw timeout - jeśli po 10 sekundach nie ma lokalizacji, zwróć null
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                fusedLocationClient.removeLocationUpdates(locationCallback)
                onResult(null)
            }, 10000)

        } catch (e: SecurityException) {
            Timber.e(e, "Security exception when requesting location updates")
            onResult(null)
        }
    }

    /**
     * Sprawdza czy lokalizacja jest włączona
     */
    suspend fun isLocationEnabled(): Boolean = suspendCancellableCoroutine { continuation ->
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_LOW_POWER, 
            10000
        ).build()
        
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            
        val settingsClient = LocationServices.getSettingsClient(context)
        
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                continuation.resume(true)
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }

    /**
     * Sprawdza czy aplikacja ma uprawnienia do lokalizacji
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
