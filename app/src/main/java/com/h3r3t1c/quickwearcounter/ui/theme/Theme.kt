package com.h3r3t1c.quickwearcounter.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme

@Composable
fun QuickTallyTheme(
    content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for your app.
     * See: https://developer.android.com/jetpack/compose/designsystems/custom
     */
    val context = LocalContext.current

    MaterialTheme(
        content = content,
        colorScheme = dynamicColorScheme(context) ?: ColorScheme()
    )
}