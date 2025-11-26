package com.example.cityreporter.presentation.screens.report

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cityreporter.data.models.ReportCategory
import com.example.cityreporter.presentation.viewmodels.CreateReportViewModel
import com.example.cityreporter.utils.Constants
import com.example.cityreporter.utils.ImageHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import timber.log.Timber
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateReportScreen(
    onNavigateBack: () -> Unit,
    onReportCreated: (String) -> Unit,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    // Diagnostic log
    LaunchedEffect(Unit) {
        Timber.d("CreateReportScreen: Proper screen loaded!")
    }
    
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Category dropdown state
    var categoryExpanded by remember { mutableStateOf(false) }
    
    // Camera permissions
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Image helper
    val imageHelper = remember { ImageHelper(context) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri?.let { uri ->
                    viewModel.addPhoto(uri)
                    Timber.d("Photo captured: $uri")
                }
            }
        }
    )
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.addPhoto(it)
                Timber.d("Photo selected from gallery: $it")
            }
        }
    )
    
    // Request location on first load
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted && uiState.location == null) {
            viewModel.getCurrentLocation()
        }
    }
    
    // Navigate after successful creation
    LaunchedEffect(uiState.submitSuccess, uiState.createdReportId) {
        if (uiState.submitSuccess) {
            uiState.createdReportId?.let { reportId ->
                Timber.d("CreateReportScreen: Navigating to report detail with ID: $reportId")
                onReportCreated(reportId)
                viewModel.reset()
            } ?: run {
                Timber.e("CreateReportScreen: submitSuccess is true but createdReportId is null!")
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nowe zgłoszenie") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // Title field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Tytuł zgłoszenia *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                singleLine = true,
                isError = uiState.error?.contains("Tytuł") == true
            )
            
            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.category.getDisplayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategoria *") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    ReportCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.getDisplayName()) },
                            onClick = {
                                viewModel.updateCategory(category)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Description field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Opis problemu *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(120.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Description, 
                        contentDescription = null
                    )
                },
                maxLines = 5,
                isError = uiState.error?.contains("Opis") == true
            )
            
            // Location section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.location != null) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Lokalizacja *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (uiState.location != null) {
                                Text(
                                    uiState.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    "Kliknij aby pobrać lokalizację",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        
                        if (uiState.isLocationLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            FilledTonalButton(
                                onClick = {
                                    if (!locationPermissions.allPermissionsGranted) {
                                        locationPermissions.launchMultiplePermissionRequest()
                                    } else {
                                        viewModel.getCurrentLocation()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pobierz")
                            }
                        }
                    }
                }
            }
            
            // Photos section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Zdjęcia (${uiState.photos.size}/${Constants.MAX_PHOTOS_PER_REPORT})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Camera button
                            FilledTonalButton(
                                onClick = {
                                    if (!cameraPermission.status.isGranted) {
                                        cameraPermission.launchPermissionRequest()
                                    } else {
                                        val photoFile = imageHelper.createImageFile()
                                        photoUri = imageHelper.getUriForFile(photoFile)
                                        cameraLauncher.launch(photoUri!!)
                                    }
                                },
                                enabled = uiState.photos.size < Constants.MAX_PHOTOS_PER_REPORT
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            // Gallery button
                            FilledTonalButton(
                                onClick = {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = uiState.photos.size < Constants.MAX_PHOTOS_PER_REPORT
                            ) {
                                Icon(
                                    Icons.Default.Photo,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    
                    if (uiState.photos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.photos) { uri ->
                                PhotoThumbnail(
                                    uri = uri,
                                    onRemove = { viewModel.removePhoto(uri) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Address field (optional)
            OutlinedTextField(
                value = uiState.address,
                onValueChange = viewModel::updateAddress,
                label = { Text("Adres (zostanie wypełniony automatycznie)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null)
                },
                singleLine = true
            )
            
            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            // Submit button
            Button(
                onClick = { viewModel.submitReport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                enabled = !uiState.isSubmitting
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wyślij zgłoszenie")
                }
            }
        }
    }
}

@Composable
fun PhotoThumbnail(
    uri: Uri,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier.size(100.dp)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Usuń",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}