package com.example.cityreporter.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityreporter.data.models.Report
import com.example.cityreporter.data.models.ReportStatus
import com.example.cityreporter.presentation.components.BottomNavigationBar
import com.example.cityreporter.presentation.components.ReportCard
import com.example.cityreporter.presentation.viewmodels.ReportViewModel
import com.example.cityreporter.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val reportsState by viewModel.reportsState.collectAsState()
    val myReportsState by viewModel.myReportsState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var filterStatus by remember { mutableStateOf<ReportStatus?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> viewModel.loadReports() // All reports
            1 -> viewModel.loadMyReports() // My reports
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zgłoszenia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Badge(
                            containerColor = if (filterStatus != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surface
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtruj")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = 1,
                onHomeClick = onNavigateBack,
                onReportsClick = { },
                onMapClick = onNavigateBack,
                onProfileClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Wszystkie") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Moje zgłoszenia") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
            
            // Content - select correct state based on tab
            val currentReports: Resource<List<Report>> = if (selectedTab == 0) reportsState else myReportsState
            
            when (val reportsResource = currentReports) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success<List<Report>> -> {
                    val currentFilterStatus = filterStatus
                    val allReports = reportsResource.data ?: emptyList()
                    val filteredReports = if (currentFilterStatus != null) {
                        allReports.filter { it.status == currentFilterStatus }
                    } else {
                        allReports
                    }
                    
                    if (filteredReports.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (currentFilterStatus != null) 
                                        "Brak zgłoszeń o statusie: ${currentFilterStatus.getDisplayName()}"
                                    else if (selectedTab == 1)
                                        "Nie masz jeszcze żadnych zgłoszeń"
                                    else
                                        "Brak zgłoszeń",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Show filter chip if active
                            val currentFilter = filterStatus
                            if (currentFilter != null) {
                                item {
                                    FilterChip(
                                        selected = true,
                                        onClick = { filterStatus = null },
                                        label = { 
                                            Text("Status: ${currentFilter.getDisplayName()}") 
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Usuń filtr",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                }
                            }
                            
                            items(
                                items = filteredReports,
                                key = { report: Report -> report.id }
                            ) { report: Report ->
                                ReportCard(
                                    report = report,
                                    onClick = { onNavigateToDetail(report.id) }
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                text = reportsResource.message ?: "Wystąpił błąd",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { 
                                    if (selectedTab == 0) {
                                        viewModel.loadReports()
                                    } else {
                                        viewModel.loadMyReports()
                                    }
                                }
                            ) {
                                Text("Spróbuj ponownie")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Filter dialog
    if (showFilterDialog) {
        FilterDialog(
            currentFilter = filterStatus,
            onFilterSelected = { status ->
                filterStatus = status
                showFilterDialog = false
            },
            onClearFilter = {
                filterStatus = null
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun FilterDialog(
    currentFilter: ReportStatus?,
    onFilterSelected: (ReportStatus) -> Unit,
    onClearFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtruj po statusie") },
        text = {
            Column {
                ReportStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentFilter == status,
                            onClick = { onFilterSelected(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status.getDisplayName())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClearFilter) {
                Text("Wyczyść")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
