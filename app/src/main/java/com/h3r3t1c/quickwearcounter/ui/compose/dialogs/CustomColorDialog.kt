package com.h3r3t1c.quickwearcounter.ui.compose.dialogs


import android.content.res.ColorStateList
import android.text.SpannableStringBuilder
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.ext.checkerboard
import com.h3r3t1c.quickwearcounter.ext.toContrastColor
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch


@Composable
fun CustomColorDialog(visible: Boolean, onDismiss: () -> Unit, onColorSelected: (Int) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        visible = visible,
        onDismissRequest = onDismiss
    ) {
        val focusRequester = remember { FocusRequester() }
        var textColor by remember { mutableStateOf("") }
        var color by remember {
            mutableStateOf(hexToColor(textColor))
        }
        val editText = remember {
            EditText(context)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .checkerboard()
                    .background(color),
                contentAlignment = Alignment.BottomCenter
            ){
                Text(
                    text = stringResource(R.string.custom_color),
                    modifier = Modifier
                        .fillMaxWidth(WearStyleHelper.HEADER_WIDTH)
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    color = color.toContrastColor()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth(WearStyleHelper.CHIP_WIDTH)
                    .focusable()
                    .focusRequester(focusRequester),
                factory = { context ->
                    val view = editText
                    view.isSingleLine = true
                    view.text = SpannableStringBuilder("")
                    view.setBackgroundResource(R.drawable.input_background)
                    view.setOnEditorActionListener { tv, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            color = hexToColor(tv.text.trim().toString())
                        }
                        false
                    }
                    view.imeOptions = EditorInfo.IME_ACTION_DONE
                    view.hint = "#RGB, #ARGB, #RRGGBB, #AARRGGBB"
                    view.setTextColor(ColorStateList.valueOf(Color.White.toArgb()))
                    view.setHintTextColor(Color.White.toArgb())
                    view
                },
                update = {
                    coroutineScope.launch {
                        awaitFrame()
                        it.requestFocus()
                        it.setSelection(it.text.length)
                    }
                }
            )
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                AlertDialogDefaults.DismissButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                AlertDialogDefaults.ConfirmButton(
                    onClick = {
                        onColorSelected(color.toArgb())
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

private fun hexToColor(hex: String): Color {
    val processedColor = hex.uppercase().removePrefix("#").trim()

    val colorInt = try{
        when (processedColor.length) {

            3 -> { // Short RGB format (#RGB)
                val (r, g, b) = processedColor.map { it.toString().repeat(2) }
                "#FF$r$g$b".toColorInt()
            }

            4 -> { // Short ARGB format (#ARGB)
                val (a, r, g, b) = processedColor.map { it.toString().repeat(2) }
                "#$a$r$g$b".toColorInt()
            }

            6 -> "#FF$processedColor".toColorInt()
            8 -> "#$processedColor".toColorInt()

            else -> {
                Color.Black.toArgb()
            }
        }
    }catch (_: Exception){
        Color.Black.toArgb()
    }
    return Color(colorInt)
}