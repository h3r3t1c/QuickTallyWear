package com.h3r3t1c.quickwearcounter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.wear.compose.material3.AlertDialogContent
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.complication.TallyComplicationService
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.ext.dataStore
import com.h3r3t1c.quickwearcounter.presentation.theme.QuickTallyTheme
import com.h3r3t1c.quickwearcounter.tile.TallyTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmResetCountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            QuickTallyTheme {
                AlertDialogContent(
                    confirmButton = {
                        AlertDialogDefaults.ConfirmButton({
                            coroutineScope.launch(Dispatchers.Default) {
                                dataStore.updateData { prefs ->
                                    prefs.toMutablePreferences().apply {
                                        set(intPreferencesKey(DataStorePrefs.KEY_CURRENT_COUNT), 0)
                                    }
                                }
                                TallyComplicationService.updateAll(applicationContext)
                                TallyTileService.setNeedUpdate()
                                withContext(Dispatchers.Main){
                                    finish()
                                }
                            }
                        })
                    },
                    title = {
                        Text(stringResource(R.string.reset_count))
                    },
                    dismissButton = {
                        AlertDialogDefaults.DismissButton({
                            finish()
                        })
                    },
                    modifier = Modifier.background(Color.Black),
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_reset),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    text = {
                        Text(stringResource(R.string.confirm_reset_count))
                    },
                    verticalArrangement = AlertDialogDefaults.VerticalArrangement,
                    contentPadding = AlertDialogDefaults.confirmDismissWithIconContentPadding(),
                    content = null,
                )
            }
        }
    }
}