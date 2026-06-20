package com.h3r3t1c.quickwearcounter.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DarkColor = Color(0xFFC0C0C0)
private val LightColor = Color(0xFFF0F0F0)
private val SquareSize = 16.dp

fun Modifier.checkerboard(squareSize: Dp = SquareSize) = this
    .clipToBounds()
    .drawWithCache {
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


// ModifierExt.kt
fun Modifier.partialBorder(
    width: Dp,
    color: Color,
    top: Boolean = true,
    bottom: Boolean = true,
    start: Boolean = true,
    end: Boolean = true
) = this.drawBehind {
    val strokeWidth = width.toPx()
    val halfStroke = strokeWidth / 2
    val w = size.width
    val h = size.height
    val r = h / 2

    // Start (Left Arc)
    if (start) {
        drawArc(
            color = color,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(halfStroke, halfStroke),
            size = Size(h - strokeWidth, h - strokeWidth),
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }

    // End (Right Arc)
    if (end) {
        drawArc(
            color = color,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w - h + halfStroke, halfStroke),
            size = Size(h - strokeWidth, h - strokeWidth),
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }

    // Top Line
    if (top) {
        drawLine(
            color = color,
            start = Offset(r, halfStroke),
            end = Offset(w - r, halfStroke),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }

    // Bottom Line
    if (bottom) {
        drawLine(
            color = color,
            start = Offset(r, h - halfStroke),
            end = Offset(w - r, h - halfStroke),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
