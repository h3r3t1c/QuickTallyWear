package com.h3r3t1c.quickwearcounter.ui.compose.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.IconButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.toColor
import com.h3r3t1c.quickwearcounter.ext.toContrastColor
import com.h3r3t1c.quickwearcounter.ui.compose.WearStyleHelper
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding
import com.h3r3t1c.quickwearcounter.ui.compose.nav.NavDestinations

@Composable
fun HomeScreen(navController: NavHostController, prefs: Preferences) {
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel()
    val feedback = LocalHapticFeedback.current
    val containerColor = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)].let { color -> color?.toColor() ?: MaterialTheme.colorScheme.primary }
    val contentColor = prefs[intPreferencesKey(DataStorePrefs.KEY_APP_THEME_COLOR)].let { color -> color?.toColor()?.toContrastColor() ?: MaterialTheme.colorScheme.onPrimary }
    val count = prefs[intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT)] ?: 0
    ScreenScaffold(
        timeText = {},
        modifier = Modifier.fillMaxSize().background(Color.Black),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ClickButton(R.drawable.ic_plus, containerColor, contentColor) {
                viewModel.updateCount(context, count + 1)
                feedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            }

            CenterCount(
                count = count,
                containerColor = containerColor,
                contentColor = contentColor,
                onReset = {
                    viewModel.updateCount(context, 0)
                    feedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
    }
}

@Composable
private fun ColumnScope.CenterCount(count: Int, containerColor: Color, contentColor: Color, onReset: () -> Unit, onOpenSettings: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ActionButton(
            icon = R.drawable.ic_reset,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = onReset
        )
        BasicText(
            text = count.toString(),
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
            style = TextStyle(color = Color.White, textAlign = TextAlign.Center),
            modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
        )
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
            shape = MaterialTheme.shapes.medium,
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
            modifier = Modifier.size(if(isLarge) 32.dp else 24.dp)
        )
    }
}

@Composable
private fun ClickButton(icon: Int, containerColor: Color, contentColor: Color, onClick: () -> Unit){
    val isLarge = WearStyleHelper.isLargeScreen()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if(isLarge) 56.dp else 48.dp)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(if(isLarge) 32.dp else 24.dp)
        )
    }
}