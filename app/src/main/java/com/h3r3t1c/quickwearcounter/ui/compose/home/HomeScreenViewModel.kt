package com.h3r3t1c.quickwearcounter.ui.compose.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

    fun updateCount(context: Context, count: Int){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateInt(context, DataStorePrefs.KEY_CURRENT_COUNT, count)
        }
    }
}