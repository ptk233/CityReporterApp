package com.example.cityreporter.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Splash : Screen("splash")
    
    object Auth : Screen("auth")
    object Login : Screen("auth/login")
    object Register : Screen("auth/register")
    
    object Main : Screen("main")
    object Home : Screen("home")
    object CreateReport : Screen("create_report")
    object ReportDetail : Screen(
        route = "report_detail/{reportId}",
        arguments = listOf(
            navArgument("reportId") { type = NavType.StringType }
        )
    ) {
        fun createRoute(reportId: String) = "report_detail/$reportId"
    }
    object ReportsList : Screen("reports_list")
    object Map : Screen("map")
    object Profile : Screen("profile")
}