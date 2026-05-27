package com.h3r3t1c.quickwearcounter.ui.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding

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

        }
    }
}