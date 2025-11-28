package com.piyush.bmusurat.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object ProgramDetail : Screen("program_detail/{shortName}") {
        fun createRoute(shortName: String) = "program_detail/$shortName"
    }
}