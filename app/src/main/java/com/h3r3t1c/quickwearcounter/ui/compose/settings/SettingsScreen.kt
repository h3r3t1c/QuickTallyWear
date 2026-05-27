package com.h3r3t1c.quickwearcounter.ui.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.toColor
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding
import com.h3r3t1c.quickwearcounter.ui.compose.dialogs.ColorPickerDialog

@Composable
fun SettingsScreen(navController: NavHostController, prefs: Preferences) {
    val viewModel: SettingsScreenViewModel = viewModel()

    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0
    )
    val padding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )
    ScreenScaffold(
        scrollState = listState,
        contentPadding = padding
    ) {internalPadding ->
        ScalingLazyColumn(
            state = listState,
            contentPadding = internalPadding,
            autoCentering = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            item(key = "settings_screen_title"){
                ListHeader {
                    Text(text = stringResource(R.string.settings), textAlign = TextAlign.Center)
                }
            }
            item(key = DataStorePrefs.KEY_APP_THEME_COLOR){
                ColorOption(
                    title = stringResource(R.string.tally_theme_color),
                    color = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)]?.toColor() ?: MaterialTheme.colorScheme.primary
                ) {
                    viewModel.openDialog(SettingsDialogState.THEME_COLOR_PICKER)
                }
            }
        }
    }
    Dialogs(viewModel)
}

@Composable
private fun Dialogs(viewModel: SettingsScreenViewModel){
    val context = LocalContext.current
    ColorPickerDialog(
        visible = viewModel.dialogState == SettingsDialogState.THEME_COLOR_PICKER,
        { viewModel.closeDialog() }
    ) {
        viewModel.closeDialog()
        viewModel.updateInt(context, DataStorePrefs.KEY_APP_THEME_COLOR, it)
    }
}

@Composable
private fun ColorOption(title: String, color: Color, onClick: () -> Unit){
    ClickOption(
        title,
        {
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .size(ButtonDefaults.IconSize)
            )
        },
        onClick
    )
}

@Composable
private fun ClickOption(title: String, icon: @Composable (BoxScope) -> Unit, onClick: () -> Unit){
    Button(
        onClick = onClick,
        label = {
            Text(text = title)
        },
        icon = icon,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    )
}