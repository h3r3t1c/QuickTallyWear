package com.h3r3t1c.quickwearcounter.ui.compose.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.OpenOnPhoneDialog
import androidx.wear.compose.material3.OpenOnPhoneDialogDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.SwitchButtonDefaults
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.openOnPhoneDialogCurvedText
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.toColor
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.FontScaleIndependent
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding
import com.h3r3t1c.quickwearcounter.ui.compose.dialogs.ColorPickerDialog

@Composable
fun SettingsScreen(navController: NavHostController, prefs: Preferences) {
    val viewModel: SettingsScreenViewModel = viewModel()
    val context = LocalContext.current
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

            items(viewModel.options.size, key = { index -> viewModel.options[index].key }) { index ->
                when(val option = viewModel.options[index]){
                    is SettingsOption.ColorOption -> {
                        ColorOption(
                            title = stringResource(option.titleResource),
                            color = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)]?.toColor() ?: MaterialTheme.colorScheme.primary
                        ) {
                            viewModel.openDialog(option.key)
                        }
                    }
                    is SettingsOption.InfoOption -> {
                        InfoOption(option)
                    }
                    is SettingsOption.SectionHeader -> {
                        ListHeader {
                            Text(text = stringResource(option.titleResource), textAlign = TextAlign.Center)
                        }
                    }
                    is SettingsOption.ClickOption -> {
                        ClickOption(option)
                    }
                    is SettingsOption.AppOption ->{
                        AppOption(option)
                    }
                    is SettingsOption.SwitchOption -> {
                        SwitchOption(option.titleResource, option.subTitleResource, prefs[booleanPreferencesKey(option.key)] ?: false){newValue ->
                            viewModel.updateBoolean(context, option.key, newValue)
                        }
                    }
                    is SettingsOption.NavDestinationOption -> {
                        NavDestinationOption(option) {
                            navController.navigate(option.destination)
                        }
                    }
                }
            }
        }
    }
    if(viewModel.loading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) { /* block touch input */ },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    Dialogs(viewModel)
}

@Composable
private fun NavDestinationOption(option: SettingsOption.NavDestinationOption, onClick: () -> Unit){
    Button(
        onClick = onClick,
        label = {
            Text(text = stringResource(option.titleResource))
        },
        icon = {
            //OptionIcon(option.iconResource, stringResource(option.titleResource))
        },
        secondaryLabel = option.subtitleResource?.let {
            { Text(text = stringResource(it), maxLines = 4) }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    )
}

@Composable
private fun SwitchOption(titleResource: Int, subtitleResource: Int?, value: Boolean, onCheckedChange: (Boolean) -> Unit){
    SwitchButton(
        checked = value,
        onCheckedChange = onCheckedChange,
        label = {
            Text(text = stringResource(titleResource))
        },
        secondaryLabel = subtitleResource?.let {
            { Text(stringResource(it), maxLines = 4) }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = SwitchButtonDefaults.switchButtonColors()
    )
}

@Composable
private fun AppOption(option: SettingsOption.AppOption){
    Button(
        onClick = option.onClick,
        label = {
            Text(text = stringResource(option.titleResource))
        },
        icon = {
            Image(
                imageVector = ImageVector.vectorResource(option.iconResource),
                contentDescription = stringResource(option.titleResource),
                modifier = Modifier
                    .size(if (WearStyleHelper.isLargeScreen()) ButtonDefaults.ExtraLargeIconSize else ButtonDefaults.LargeIconSize)
                    .background(option.iconBackgroundColor, MaterialTheme.shapes.small)
                    .padding(4.dp)
            )
        },

        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    )
}

@Composable
private fun ClickOption(option: SettingsOption.ClickOption){
    Button(
        onClick = option.onClick,
        label = {
            Text(text = stringResource(option.titleResource))
        },
        icon = {
            OptionIcon(option.iconResource, stringResource(option.titleResource))
        },
        secondaryLabel = option.subtitleResource?.let {
            { Text(text = stringResource(it), maxLines = 3) }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    )
}

@Composable
private fun OptionIcon(iconResource: Int, contentDescription: String){
    Icon(
        imageVector = ImageVector.vectorResource(iconResource),
        contentDescription = contentDescription,
        modifier = Modifier.size(if(WearStyleHelper.isLargeScreen()) ButtonDefaults.LargeIconSize else ButtonDefaults.IconSize)
    )
}

@Composable
private fun InfoOption(option: SettingsOption.InfoOption){
    Button(
        onClick = {  },
        enabled = false,
        icon = {
            OptionIcon(option.iconResource, stringResource(option.titleResource))
        },
        label = {
            Text(text = stringResource(option.titleResource))
        },
        secondaryLabel = {
            Text(text = option.value)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
            disabledSecondaryContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun Dialogs(viewModel: SettingsScreenViewModel){
    val context = LocalContext.current
    val resources = LocalResources.current
    ColorPickerDialog(
        visible = viewModel.openDialog == DataStorePrefs.KEY_APP_THEME_COLOR,
        { viewModel.closeDialog() }
    ) {
        viewModel.closeDialog()
        viewModel.updateInt(context, DataStorePrefs.KEY_APP_THEME_COLOR, it)
    }
    FontScaleIndependent { // has to be font scale independent because it will not draw correctly if font scale > 1
        val dialogStyle = OpenOnPhoneDialogDefaults.curvedTextStyle
        OpenOnPhoneDialog(
            visible = viewModel.openDialog == SettingsScreenViewModel.KEY_DIALOG_SHOW_CONTINUE_ON_PHONE,
            onDismissRequest = { viewModel.closeDialog() },
            curvedText = { openOnPhoneDialogCurvedText(text = resources.getString(R.string.continue_on_phone), style = dialogStyle) },
        )
    }

}

@Composable
private fun ColorOption(title: String, color: Color, onClick: () -> Unit){
    Button(
        onClick = onClick,
        label = {
            Text(text = title)
        },
        icon = {
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .size(if (WearStyleHelper.isLargeScreen()) ButtonDefaults.LargeIconSize else ButtonDefaults.IconSize)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors()
    )
}