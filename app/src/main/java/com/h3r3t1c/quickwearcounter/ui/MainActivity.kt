
package com.h3r3t1c.quickwearcounter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.h3r3t1c.quickwearcounter.ext.dataStore
import com.h3r3t1c.quickwearcounter.presentation.theme.QuickTallyTheme
import com.h3r3t1c.quickwearcounter.ui.compose.nav.Nav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTallyTheme {
                Nav()
            }
        }
    }
}