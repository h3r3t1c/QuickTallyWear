
package com.h3r3t1c.quickwearcounter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.h3r3t1c.quickwearcounter.presentation.theme.QuickTallyTheme
import com.h3r3t1c.quickwearcounter.ui.compose.nav.Nav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            QuickTallyTheme {
                Nav()
            }
        }
    }
}