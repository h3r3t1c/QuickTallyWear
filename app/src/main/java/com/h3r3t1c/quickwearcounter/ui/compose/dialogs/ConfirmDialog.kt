package com.h3r3t1c.quickwearcounter.ui.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text

@Composable
fun ConfirmDialog(visible: Boolean, title: String, text: String, icon: Int, onDismiss: () -> Unit, onConfirm: () -> Unit){
    AlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.Black),
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        icon = {
           Icon(
               imageVector = ImageVector.vectorResource(icon),
               contentDescription = null,
               tint = Color.White,
               modifier = Modifier.size(24.dp)
           )
        },
        confirmButton = {
            AlertDialogDefaults.ConfirmButton(onConfirm)
        },
        dismissButton = {
            AlertDialogDefaults.DismissButton(onDismiss)
        }
    )
}