package com.example.cityreporter.presentation.screens.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityreporter.data.models.Report
import com.example.cityreporter.data.models.ReportCategory
import com.example.cityreporter.presentation.viewmodels.MapViewModel
import com.example.cityreporter.presentation.viewmodels.ReportViewModel
import com.example.cityreporter.utils.Constants
import com.example.cityreporter.utils.Resource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    mapViewModel: MapViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val nearbyReports by reportViewModel.nearbyReportsState.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val isLocationLoading by mapViewModel.isLocationLoading.collectAsState()
    
    // Default location (Wrocław)
    val defaultLocation = LatLng(51.1078852, 17.0385376)
    
    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultLocation,
            Constants.DEFAULT_MAP_ZOOM
        )
    }
    
    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Request location when screen loads
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            mapViewModel.getCurrentLocation()
        }
    }
    
    // Load nearby reports when location changes
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            reportViewModel.loadNearbyReports(
                latitude = it.latitude,
                longitude = it.longitude,
                radius = Constants.DEFAULT_SEARCH_RADIUS_KM
            )
        }
    }
    
    // Move camera when location is obtained
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    Constants.DEFAULT_MAP_ZOOM
                )
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa zgłoszeń") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    if (isLocationLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!locationPermissions.allPermissionsGranted) {
                // Show permission request
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOff,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Potrzebujemy dostępu do lokalizacji",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Aby pokazać zgłoszenia w Twojej okolicy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { locationPermissions.launchMultiplePermissionRequest() }
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Włącz lokalizację")
                    }
                }
            } else {
                // Show map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = true,
                        mapToolbarEnabled = false,
                        compassEnabled = true
                    )
                ) {
                    // Add markers for reports
                    when (nearbyReports) {
                        is Resource.Success -> {
                            nearbyReports.data?.forEach { report ->
                                ReportMarker(
                                    report = report,
                                    onClick = { onNavigateToDetail(report.id) }
                                )
                            }
                        }
                        else -> { /* Handle loading/error states if needed */ }
                    }
                }
                
                // Floating button to center on current location
                FloatingActionButton(
                    onClick = {
                        mapViewModel.getCurrentLocation()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Moja lokalizacja"
                    )
                }
                
                // Show report count
                if (nearbyReports is Resource.Success) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Znaleziono zgłoszeń: ${nearbyReports.data?.size ?: 0}",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMarker(
    report: Report,
    onClick: () -> Unit
) {
    val markerState = rememberMarkerState(
        position = LatLng(report.latitude, report.longitude)
    )
    
    Marker(
        state = markerState,
        title = report.title,
        snippet = "${report.category.getDisplayName()} - ${report.status.getDisplayName()}",
        icon = BitmapDescriptorFactory.defaultMarker(
            getMarkerColor(report.category)
        ),
        onClick = {
            onClick()
            false // Return false to show info window
        }
    )
}

fun getMarkerColor(category: ReportCategory): Float {
    return when (category) {
        ReportCategory.ROAD_DAMAGE -> BitmapDescriptorFactory.HUE_RED
        ReportCategory.LIGHTING -> BitmapDescriptorFactory.HUE_YELLOW
        ReportCategory.TRAFFIC_SIGNS -> BitmapDescriptorFactory.HUE_BLUE
        ReportCategory.ILLEGAL_DUMPING -> BitmapDescriptorFactory.HUE_ORANGE
        ReportCategory.VANDALISM -> BitmapDescriptorFactory.HUE_VIOLET
        ReportCategory.GREEN_AREAS -> BitmapDescriptorFactory.HUE_GREEN
        ReportCategory.SIDEWALK -> BitmapDescriptorFactory.HUE_CYAN
        ReportCategory.PLAYGROUND -> BitmapDescriptorFactory.HUE_MAGENTA
        ReportCategory.PUBLIC_TRANSPORT -> BitmapDescriptorFactory.HUE_AZURE
        ReportCategory.OTHER -> BitmapDescriptorFactory.HUE_ROSE
    }
}