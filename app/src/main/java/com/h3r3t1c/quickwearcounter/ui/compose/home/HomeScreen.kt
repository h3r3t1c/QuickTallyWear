package com.h3r3t1c.quickwearcounter.ui.compose.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.toColor
import com.h3r3t1c.quickwearcounter.ext.toContrastColor
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import com.h3r3t1c.quickwearcounter.ui.compose.dialogs.ConfirmDialog
import com.h3r3t1c.quickwearcounter.ui.compose.dialogs.EditTextDialog
import com.h3r3t1c.quickwearcounter.ui.compose.nav.NavDestinations

@Composable
fun HomeScreen(navController: NavHostController, prefs: Preferences) {
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel()
    val feedback = LocalHapticFeedback.current
    val containerColor = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)].let { color -> color?.toColor() ?: MaterialTheme.colorScheme.primary }
    val contentColor = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)].let { color -> color?.toColor()?.toContrastColor() ?: MaterialTheme.colorScheme.onPrimary }
    val count = prefs[intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT)] ?: 0
    val focusRequester = remember {
        FocusRequester()
    }
    ScreenScaffold(
        timeText = {},
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent {
                    val delta = it.verticalScrollPixels.toInt()
                    viewModel.updateRotaryTravel(context, delta, count) {
                        feedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            ClickButton(R.drawable.ic_plus, containerColor, contentColor) {
                viewModel.updateCount(context, count + 1)
                feedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            }

            CenterCount(
                count = count,
                containerColor = containerColor,
                contentColor = contentColor,
                onEditCount = {
                    viewModel.openDialog(HomeScreenDialogState.EDIT_COUNT)
                },
                onReset = {
                    viewModel.openDialog(HomeScreenDialogState.CONFIRM_RESET_COUNT)
                },
                onOpenSettings = {
                    navController.navigate(NavDestinations.SETTINGS)
                }
            )

            ClickButton(R.drawable.ic_minus, containerColor, contentColor) {
                viewModel.updateCount(context, count - 1)
                feedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            }
        }
        Dialogs(count, viewModel)
    }
    // have to use this to fixed loss of focus when navigating to settings screen
    // LaunchedEffect does not work
    LifecycleResumeEffect(Unit) {
        focusRequester.requestFocus()
        onPauseOrDispose {  }
    }

}

@Composable
private fun Dialogs(count: Int, viewModel: HomeScreenViewModel){
    val feedback = LocalHapticFeedback.current
    val context = LocalContext.current
    ConfirmDialog(
        visible = viewModel.dialogState == HomeScreenDialogState.CONFIRM_RESET_COUNT,
        title = stringResource(R.string.reset_count),
        text = stringResource(R.string.confirm_reset_count),
        icon = R.drawable.ic_reset,
        onDismiss = { viewModel.closeDialog() },
        onConfirm = {
            viewModel.updateCount(context, 0)
            feedback.performHapticFeedback(HapticFeedbackType.Confirm)
            viewModel.closeDialog()
        }
    )
    EditTextDialog(
        visible = viewModel.dialogState == HomeScreenDialogState.EDIT_COUNT,
        value = count,
        onDismiss = { viewModel.closeDialog() },
        onUpdate = {
            viewModel.updateCount(context, it)
            feedback.performHapticFeedback(HapticFeedbackType.Confirm)
            viewModel.closeDialog()
        }
    )
}

@Composable
private fun ColumnScope.CenterCount(count: Int, containerColor: Color, contentColor: Color,onEditCount: () -> Unit, onReset: () -> Unit, onOpenSettings: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ActionButton(
            icon = R.drawable.ic_reset,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onReset
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable(onClick = onEditCount),
            contentAlignment = Alignment.Center
        ){
            BasicText(
                text = count.toString(),
                autoSize = TextAutoSize.StepBased(),
                maxLines = 1,
                style = TextStyle(color = Color.White, textAlign = TextAlign.Center),
            )
        }
        ActionButton(
            icon = R.drawable.ic_settings,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onOpenSettings
        )
    }
}

@Composable
private fun ActionButton(icon: Int, containerColor: Color, contentColor: Color, onClick: () -> Unit){
    val isLarge = WearStyleHelper.isLargeScreen()
    IconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.animatedShapes(
            shape = if(isLarge) MaterialTheme.shapes.medium else RoundedCornerShape(14.dp),
            pressedShape = MaterialTheme.shapes.small
        ),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.size(if(isLarge) 48.dp else 40.dp )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ClickButton(icon: Int, containerColor: Color, contentColor: Color, onClick: () -> Unit){
    val isLarge = WearStyleHelper.isLargeScreen()
    IconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.animatedShapes(
            shape = CircleShape,
            pressedShape = MaterialTheme.shapes.medium
        ),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLarge) 66.dp else 56.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(if(isLarge) 40.dp else 32.dp)
        )
    }
}