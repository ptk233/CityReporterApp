package com.example.cityreporter.presentation.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cityreporter.data.models.Report
import com.example.cityreporter.data.models.ReportStatus
import com.example.cityreporter.presentation.components.StatusBadge
import com.example.cityreporter.presentation.viewmodels.ReportViewModel
import com.example.cityreporter.utils.Resource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val reportState by viewModel.reportDetailState.collectAsState()
    
    LaunchedEffect(reportId) {
        viewModel.loadReportDetail(reportId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły zgłoszenia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (reportState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                reportState?.data?.let { report ->
                    ReportDetailContent(
                        report = report,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = reportState?.message ?: "Błąd ładowania zgłoszenia",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadReportDetail(reportId) }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }
            null -> {
                // Initial state
            }
        }
    }
}

@Composable
fun ReportDetailContent(
    report: Report,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with title and status
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AssistChip(
                                    onClick = { },
                                    label = { Text(report.category.getDisplayName()) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Category,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                                StatusBadge(
                                    status = report.status.getDisplayName(),
                                    color = Color(report.status.getColor())
                                )
                            }
                        }
                        
                        // Priority indicator
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(report.priority.getColor()).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = report.priority.getDisplayName(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(report.priority.getColor()),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        // Photos
        if (report.photoUrls.isNotEmpty()) {
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Zdjęcia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(report.photoUrls) { photoUrl ->
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Report photo",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Description
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Opis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        // Location
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Lokalizacja",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = report.address,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    
                    // Mini map
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        val reportLocation = LatLng(report.latitude, report.longitude)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(reportLocation, 15f)
                        }
                        
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                tiltGesturesEnabled = false,
                                rotationGesturesEnabled = false
                            )
                        ) {
                            Marker(
                                state = rememberMarkerState(position = reportLocation),
                                title = report.title
                            )
                        }
                    }
                }
            }
        }
        
        // Info
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Informacje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = "Zgłosił",
                        value = report.userName
                    )
                    InfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Data zgłoszenia",
                        value = formatDateTime(report.createdAt)
                    )
                    InfoRow(
                        icon = Icons.Default.Update,
                        label = "Ostatnia aktualizacja",
                        value = formatDateTime(report.updatedAt)
                    )
                }
            }
        }
        
        // Comments section (if any)
        if (report.comments.isNotEmpty()) {
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Komentarze (${report.comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        report.comments.forEach { comment ->
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = comment.userName,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (comment.isOfficial) {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text("Urzędnik", style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.height(24.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = formatDateTime(comment.createdAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (comment != report.comments.last()) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun formatDateTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}