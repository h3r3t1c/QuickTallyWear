package com.h3r3t1c.quickwearcounter.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColor = Color(0xFFC0C0C0)
private val LightColor = Color(0xFFF0F0F0)
private val SquareSize = 16.dp

fun Modifier.checkerboard(squareSize: Dp = SquareSize) = this.clipToBounds().drawWithCache {
    onDrawBehind {
        val squareSizePx = squareSize.toPx()
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                // Calculate color based on grid position to ensure pattern is correct
                val i = (x / squareSizePx).toInt()
                val j = (y / squareSizePx).toInt()
                val color = if ((i + j) % 2 == 0) LightColor else DarkColor

                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(squareSizePx, squareSizePx)
                )
                x += squareSizePx
            }
            y += squareSizePx
        }
    }
}