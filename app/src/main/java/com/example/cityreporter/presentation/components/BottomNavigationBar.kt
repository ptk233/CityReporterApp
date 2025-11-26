package com.example.cityreporter.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onReportsClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        BottomNavItem(
            label = "Główna",
            icon = Icons.Default.Home,
            selected = selectedIndex == 0,
            onClick = onHomeClick
        )
        BottomNavItem(
            label = "Zgłoszenia",
            icon = Icons.AutoMirrored.Filled.List,
            selected = selectedIndex == 1,
            onClick = onReportsClick
        )
        BottomNavItem(
            label = "Mapa",
            icon = Icons.Default.Map,
            selected = selectedIndex == 2,
            onClick = onMapClick
        )
        BottomNavItem(
            label = "Profil",
            icon = Icons.Default.Person,
            selected = selectedIndex == 3,
            onClick = onProfileClick
        )
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        label = { Text(label) },
        icon = { Icon(icon, contentDescription = label) },
        selected = selected,
        onClick = onClick
    )
}
