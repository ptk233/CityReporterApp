package com.example.cityreporter.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityreporter.presentation.viewmodels.AuthViewModel
import com.example.cityreporter.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = null)
    
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            delay(Constants.SPLASH_SCREEN_DURATION)
            if (isLoggedIn == true) {
                onNavigateToHome()
            } else {
                onNavigateToAuth()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "City Reporter",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            Text(
                text = "Zgłaszaj usterki miejskie",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}