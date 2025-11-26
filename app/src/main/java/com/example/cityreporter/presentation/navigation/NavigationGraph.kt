package com.example.cityreporter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.cityreporter.presentation.screens.auth.LoginScreen
import com.example.cityreporter.presentation.screens.auth.RegisterScreen
import com.example.cityreporter.presentation.screens.home.HomeScreen
import com.example.cityreporter.presentation.screens.report.CreateReportScreen
import com.example.cityreporter.presentation.screens.report.ReportDetailScreen
import com.example.cityreporter.presentation.screens.report.ReportsListScreen
import com.example.cityreporter.presentation.screens.map.MapScreen
import com.example.cityreporter.presentation.screens.profile.ProfileScreen
import com.example.cityreporter.presentation.screens.splash.SplashScreen
import com.example.cityreporter.presentation.viewmodels.AuthViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        navigation(
            startDestination = Screen.Login.route,
            route = Screen.Auth.route
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(route = Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        navigation(
            startDestination = Screen.Home.route,
            route = Screen.Main.route
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onNavigateToCreateReport = {
                        navController.navigate(Screen.CreateReport.route)
                    },
                    onNavigateToReportDetail = { reportId ->
                        navController.navigate(Screen.ReportDetail.createRoute(reportId))
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route)
                    },
                    onNavigateToReports = {
                        navController.navigate(Screen.ReportsList.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
            
            composable(route = Screen.CreateReport.route) {
                CreateReportScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onReportCreated = { reportId ->
                        navController.navigate(Screen.ReportDetail.createRoute(reportId)) {
                            popUpTo(Screen.CreateReport.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(
                route = Screen.ReportDetail.route,
                arguments = Screen.ReportDetail.arguments
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                ReportDetailScreen(
                    reportId = reportId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(route = Screen.ReportsList.route) {
                ReportsListScreen(
                    onNavigateToDetail = { reportId ->
                        navController.navigate(Screen.ReportDetail.createRoute(reportId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(route = Screen.Map.route) {
                MapScreen(
                    onNavigateToDetail = { reportId ->
                        navController.navigate(Screen.ReportDetail.createRoute(reportId))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    
    // Handle authentication state changes
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && navController.currentDestination?.route?.startsWith(Screen.Main.route) == true) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
        }
    }
}