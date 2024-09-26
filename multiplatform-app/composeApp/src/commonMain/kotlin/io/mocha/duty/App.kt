package io.mocha.duty

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.mocha.duty.pages.*

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        Surface {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize(),
                enterTransition = {
                    fadeIn() + scaleIn()
                },
                exitTransition = {
                    fadeOut() + scaleOut()
                }
            ) {
                composable(Routes.HOME) { HomePage(navController) }
                composable(Routes.HELLO) { HelloPage(navController) }
                composable(Routes.ADMIN) { AdminPage() }
            }
        }

    }
}