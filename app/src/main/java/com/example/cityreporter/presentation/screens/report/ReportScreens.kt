package com.example.cityreporter.presentation.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Usunięto CreateReportScreen - jest już zdefiniowany w CreateReportScreen.kt

@Composable
fun ReportDetailScreenPlaceholder(
    reportId: String,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Szczegóły zgłoszenia: $reportId", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Powrót")
            }
        }
    }
}

@Composable
fun ReportsListScreenPlaceholder(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Lista zgłoszeń", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Powrót")
            }
        }
    }
}
