package com.krypton.animeid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.krypton.animeid.components.BottomNavigationBarComponent
import com.krypton.animeid.screens.AnimeDetailScreen
import com.krypton.animeid.screens.HomeScreen
import com.krypton.animeid.screens.SearchScreen
import com.krypton.animeid.screens.SettingsScreen
import com.krypton.animeid.ui.theme.AnimeIDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnimeIDTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBarComponent(navController) }) { innerPadding ->

        val graph = navController.createGraph(startDestination = Screens.Home.rout) {
            composable(route = Screens.Home.rout) {
                HomeScreen(navController)
            }
            composable(route = Screens.Settings.rout) {
                SettingsScreen()
            }
            composable(route = Screens.Search.rout) {
                SearchScreen(navController)
            }

            composable(
                route = "detail/{title}/{url}/{img}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("url") { type = NavType.StringType },
                    navArgument("img") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                AnimeDetailScreen(
                    title = backStackEntry.arguments?.getString("title") ?: "",
                    url = backStackEntry.arguments?.getString("url") ?: "",
                    img = backStackEntry.arguments?.getString("img") ?: "",
                    navController = navController
                )
            }

        }

        NavHost(
            navController = navController, graph = graph, modifier = Modifier.padding(innerPadding)
        )

    }
}