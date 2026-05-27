package com.h3r3t1c.quickwearcounter.ui.compose.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.h3r3t1c.quickwearcounter.ext.dataStore
import com.h3r3t1c.quickwearcounter.ui.compose.home.HomeScreen
import com.h3r3t1c.quickwearcounter.ui.compose.settings.SettingsScreen

@Composable
fun Nav(destination: String = NavDestinations.HOME){
    val context = LocalContext.current
    val prefsFlow = remember { context.dataStore.data }
    val prefs = prefsFlow.collectAsStateWithLifecycle(null)
    val navController = rememberSwipeDismissableNavController()

    AppScaffold(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        prefs.value?.let {p ->
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = destination
            ){
                composable(NavDestinations.HOME){
                    HomeScreen(navController, p)
                }
                composable(NavDestinations.SETTINGS){
                    SettingsScreen(navController, p)
                }
            }

        }?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
}