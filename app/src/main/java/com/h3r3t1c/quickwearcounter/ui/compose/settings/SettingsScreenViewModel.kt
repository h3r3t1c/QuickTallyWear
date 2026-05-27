package com.h3r3t1c.quickwearcounter.ui.compose.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsScreenViewModel: ViewModel() {

    fun updateInt(context: Context, key: String, value: Int){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateInt(context, key, value)
        }
    }
}