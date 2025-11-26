package com.example.cityreporter.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cityreporter.presentation.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentUser?.let { user ->
                Text("Profil użytkownika", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Imię i nazwisko: ${user.name}")
                Text("Email: ${user.email}")
                Text("Punkty: ${user.points}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { 
                    authViewModel.logout()
                    onNavigateToLogin()
                }) {
                    Text("Wyloguj")
                }
            } ?: run {
                Text("Nie zalogowano")
                Button(onClick = onNavigateToLogin) {
                    Text("Zaloguj się")
                }
            }
        }
    }
}