package com.h3r3t1c.quickwearcounter.ui.compose.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsScreenViewModel: ViewModel() {

    var dialogState by mutableStateOf(SettingsDialogState.NONE)

    fun updateInt(context: Context, key: String, value: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateInt(context, key, value)
        }
    }

    fun openDialog(state: SettingsDialogState){
        dialogState = state
    }

    fun closeDialog(){
        dialogState = SettingsDialogState.NONE
    }
}
enum class SettingsDialogState{
    NONE,
    THEME_COLOR_PICKER,
}