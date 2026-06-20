package com.h3r3t1c.quickwearcounter.ui.compose.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3r3t1c.quickwearcounter.complication.TallyComplicationService
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.tile.TallyTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

    private var rotaryTravel = 0
    var dialogState by mutableStateOf(HomeScreenDialogState.NONE)

    fun updateRotaryTravel(context: Context, travel: Int, count: Int, vibrate: () -> Unit){
        if(travel > 0 && rotaryTravel < 0)
            rotaryTravel = 0
        else if(travel < 0 && rotaryTravel > 0)
            rotaryTravel = 0
        rotaryTravel += travel
        if(rotaryTravel > 50){ // i chose 50 because for some reason i've watches report like 2 or 3 as travel per degrees of rotation so a little movement causes it to change way to fast...
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
            DataStorePrefs.updateCurrentCount(context, count)
            TallyTileService.setNeedUpdate()
            TallyComplicationService.updateAll(context)
        }
    }

    fun openDialog(state: HomeScreenDialogState){
        dialogState = state
    }

    fun closeDialog(){
        dialogState = HomeScreenDialogState.NONE
    }
}
enum class HomeScreenDialogState{
    NONE,
    CONFIRM_RESET_COUNT,
    EDIT_COUNT;
}