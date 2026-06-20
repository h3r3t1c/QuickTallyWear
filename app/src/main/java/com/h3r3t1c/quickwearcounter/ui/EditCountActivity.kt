package com.h3r3t1c.quickwearcounter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.CircularProgressIndicator
import com.h3r3t1c.quickwearcounter.complication.TallyComplicationService
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.presentation.theme.QuickTallyTheme
import com.h3r3t1c.quickwearcounter.tile.TallyTileService
import com.h3r3t1c.quickwearcounter.ui.compose.dialogs.EditCountDialogContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCountActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            QuickTallyTheme {
                val coroutineScope = rememberCoroutineScope()
                var value by remember {
                    mutableStateOf<Int?>(null)
                }
                LaunchedEffect(Unit) {
                    value = DataStorePrefs.getCurrentCount(applicationContext) ?: 0
                }
                if(value == null){
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }else{
                    EditCountDialogContent(value!!, {finish()}) {
                        coroutineScope.launch(Dispatchers.Default) {
                            DataStorePrefs.updateCurrentCount(applicationContext, it)
                            TallyTileService.updateNow(applicationContext)
                            TallyComplicationService.updateAll(applicationContext)
                            withContext(Dispatchers.Main){
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}