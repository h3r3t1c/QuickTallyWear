package com.h3r3t1c.quickwearcounter.ui.compose.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

    private var rotaryTravel = 0

    fun updateRotaryTravel(context: Context, travel: Int, count: Int, vibrate: () -> Unit){
        if(travel > 0 && rotaryTravel < 0)
            rotaryTravel = 0
        else if(travel < 0 && rotaryTravel > 0)
            rotaryTravel = 0
        rotaryTravel += travel
        if(rotaryTravel > 50){
            updateCount(context, count + 1)
            vibrate()
            rotaryTravel = 0
        }else if(rotaryTravel < -50){
            updateCount(context, count - 1)
            vibrate()
            rotaryTravel = 0
        }
    }

    fun updateCount(context: Context, count: Int){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateInt(context, DataStorePrefs.KEY_CURRENT_COUNT, count)
        }
    }
}