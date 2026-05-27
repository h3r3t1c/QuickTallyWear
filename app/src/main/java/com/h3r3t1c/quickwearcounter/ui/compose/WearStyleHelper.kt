package com.h3r3t1c.quickwearcounter.ui.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

object WearStyleHelper {
    const val CHIP_WIDTH = 1f - 0.104f
    const val HEADER_WIDTH = CHIP_WIDTH - 0.146f

    const val LARGE_SCREEN_WIDTH_DP_THRESHOLD = 225

    @Composable
    fun isLargeScreen() = LocalConfiguration.current.screenWidthDp > LARGE_SCREEN_WIDTH_DP_THRESHOLD

    fun isLargeScreen(context: Context) = context.resources.configuration.screenWidthDp > LARGE_SCREEN_WIDTH_DP_THRESHOLD
}