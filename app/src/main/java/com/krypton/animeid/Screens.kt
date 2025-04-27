package com.krypton.animeid

sealed class Screens(val rout: String) {
    object Home: Screens("home_screen")
    object Settings: Screens("settings_screen")
    object Search: Screens("search_screen")
}