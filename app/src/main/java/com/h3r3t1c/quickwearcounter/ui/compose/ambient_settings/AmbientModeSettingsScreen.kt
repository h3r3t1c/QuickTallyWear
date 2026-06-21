package com.h3r3t1c.quickwearcounter.ui.compose.ambient_settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.SwitchButtonDefaults
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ui.compose.common.ColumnItemType
import com.h3r3t1c.quickwearcounter.ui.compose.common.rememberResponsiveColumnPadding
import com.h3r3t1c.quickwearcounter.util.PermissionHelper
import kotlinx.coroutines.launch

@Composable
fun AmbientModeSettingsScreen(navController: NavHostController, prefs: Preferences){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasNotificationPermission by remember { mutableStateOf(PermissionHelper.hasNotificationPermission(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permissionsGranted ->
        hasNotificationPermission = permissionsGranted
    }
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
            item{
                ListHeader {
                    Text(text = stringResource(R.string.keep_screen_visible_ambient_mode), textAlign = TextAlign.Center)
                }
            }
            item{
                Text(
                    text = stringResource(R.string.keep_screen_visible_ambient_mode_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            item{
                Button(
                    onClick = {
                        if(PermissionHelper.hasNotificationPermission(context)){
                            hasNotificationPermission = true
                            Toast.makeText(context, R.string.notification_permission_already_granted, Toast.LENGTH_SHORT).show()
                        }
                        else
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    },
                    label = {
                        Text(text = stringResource(R.string.grant_notification_permission))
                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_open),
                            contentDescription = stringResource(R.string.grant_notification_permission),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors()
                )
            }

            item{
                SwitchButton(
                    checked = prefs[booleanPreferencesKey(DataStorePrefs.KEY_KEEP_SCREEN_VISIBLE_AMBIENT_MODE)] ?: false,
                    onCheckedChange = {
                        coroutineScope.launch {
                            DataStorePrefs.updateBoolean(context, DataStorePrefs.KEY_KEEP_SCREEN_VISIBLE_AMBIENT_MODE, it)
                        }
                    },
                    enabled = hasNotificationPermission,
                    label = {
                        Text(text = "Enable Feature")
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = SwitchButtonDefaults.switchButtonColors()
                )
            }
        }
    }
}