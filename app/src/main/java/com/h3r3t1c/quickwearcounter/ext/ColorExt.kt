package com.h3r3t1c.quickwearcounter.ext

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
@Stable
fun Color.toContrastColor(): Color = if(android.graphics.Color.luminance(this.toArgb()) > 0.5f) Color.Black else Color.White
@Stable
fun Color.toAntiContrast(): Color = if(android.graphics.Color.luminance(this.toArgb()) > 0.5f) Color.White else Color.Black
@Stable
fun Int.toColor(): Color = Color(this)