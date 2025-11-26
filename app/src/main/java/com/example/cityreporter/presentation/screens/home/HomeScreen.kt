package com.example.cityreporter.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityreporter.data.models.Report
import com.example.cityreporter.presentation.components.BottomNavigationBar
import com.example.cityreporter.presentation.components.ReportCard
import com.example.cityreporter.presentation.viewmodels.AuthViewModel
import com.example.cityreporter.presentation.viewmodels.ReportViewModel
import com.example.cityreporter.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateReport: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToProfile: () -> Unit,
    reportViewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val reportsState by reportViewModel.reportsState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    
    LaunchedEffect(Unit) {
        reportViewModel.loadReports()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("City Reporter", fontWeight = FontWeight.Bold)
                        currentUser?.let {
                            Text(
                                text = "Witaj, ${it.name}!",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMap) {
                        Icon(Icons.Default.Map, contentDescription = "Mapa")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = 0,
                onHomeClick = { },
                onReportsClick = onNavigateToReports,
                onMapClick = onNavigateToMap,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateReport,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj zgłoszenie")
            }
        }
    ) { paddingValues ->
        when (reportsState) {
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
                val reports = reportsState.data ?: emptyList()
                if (reports.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Najnowsze zgłoszenia",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(reports) { report ->
                            ReportCard(
                                report = report,
                                onClick = { onNavigateToReportDetail(report.id) }
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                ErrorState(
                    message = reportsState.message ?: "Wystąpił błąd",
                    onRetry = { reportViewModel.loadReports() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Report,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Brak zgłoszeń",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Bądź pierwszy i zgłoś usterkę!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Spróbuj ponownie")
            }
        }
    }
}