package com.h3r3t1c.quickwearcounter.ui.compose.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.h3r3t1c.quickwearcounter.BuildConfig
import com.h3r3t1c.quickwearcounter.R
import com.h3r3t1c.quickwearcounter.data.DataStorePrefs
import com.h3r3t1c.quickwearcounter.tile.TallyTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsScreenViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * I know i should be using UI state object here but i dont feel like creating more objects right now...
     */

    var openDialog by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
    val options = mutableListOf<SettingsOption>()

    init {
        val context = getApplication<Application>().applicationContext
        options.add(SettingsOption.SectionHeader(R.string.settings))
        options.add(SettingsOption.ColorOption(R.string.tally_theme_color, DataStorePrefs.KEY_APP_THEME_COLOR))
        options.add(SettingsOption.SwitchOption(R.string.keep_screen_on, DataStorePrefs.KEY_KEEP_SCREEN_ON))

        options.add(SettingsOption.SectionHeader(R.string.about))
        options.add(SettingsOption.ClickOption(R.drawable.ic_google_play, R.string.play_store_page, null) {
            openLocalLink(context, "https://play.google.com/store/apps/details?id=${context.packageName}")
        })
        options.add(
            SettingsOption.ClickOption(R.drawable.ic_github,R.string.source_code, R.string.source_code_description) {
                openRemoteLink(context, "https://github.com/h3r3t1c/QuickTallyWear")
            }

        )
        options.add(SettingsOption.InfoOption(R.drawable.ic_info, R.string.version, "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"))

        options.add(SettingsOption.SectionHeader(R.string.more_apps))
        options.add(
            SettingsOption.AppOption(R.drawable.app_icon_unit_converter,Color(0xff1E88E5), R.string.app_unit_converter) {
                openLocalLink(context, "https://play.google.com/store/apps/details?id=com.h3r3t1c.wearunitconverter")
            }
        )
        options.add(
            SettingsOption.AppOption(R.drawable.app_icon_tallies,Color(0xff424242), R.string.app_tallies) {
                openLocalLink(context, "https://play.google.com/store/apps/details?id=com.h3r3t1c.prowearcounter")
            }
        )
        options.add(
            SettingsOption.AppOption(R.drawable.app_icon_cal_pro,Color(0xffffffff), R.string.app_calendar_pro) {
                openLocalLink(context, "https://play.google.com/store/apps/details?id=com.h3r3t1c.prowearcalendar")
            }
        )
        options.add(
            SettingsOption.AppOption(R.drawable.app_icon_pbow,Color(0xff424242), R.string.app_pbow) {
                openLocalLink(context, "https://play.google.com/store/apps/details?id=com.h3r3t1c.phonebatteryinfoonwear")
            }
        )
        options.add(
            SettingsOption.AppOption(R.drawable.app_icon_events_countdown,Color(0xffffffff), R.string.app_event_countdowns) {
                openLocalLink(context, "https://play.google.com/store/apps/details?id=com.h3r3t1c.wear.wearcountdown")
            }
        )
    }

    fun updateInt(context: Context, key: String, value: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateInt(context, key, value)
            if(key == DataStorePrefs.KEY_APP_THEME_COLOR){
                TallyTileService.setNeedUpdate()
            }
        }
    }

    fun updateBoolean(context: Context, key: String, value: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            DataStorePrefs.updateBoolean(context, key, value)
        }
    }

    private fun openLocalLink(context: Context, url: String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        try {
            context.startActivity(intent)
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(context, R.string.cannot_open, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openRemoteLink(context: Context, url: String){
        viewModelScope.launch(Dispatchers.Default) {
            val nodeClient = Wearable.getNodeClient(context)
            val nodes = Tasks.await(nodeClient.connectedNodes)
            if(nodes.isEmpty()){
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, R.string.not_connected_to_phone, Toast.LENGTH_SHORT).show()
                    loading = false
                }
            }else{
                var nodeId: String? = null
                for(node in nodes){
                    if(node.isNearby) {
                        nodeId = node.id
                        break
                    }
                }
                if(nodeId == null){
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, R.string.not_connected_to_phone, Toast.LENGTH_SHORT).show()
                        loading = false
                    }
                }else{
                    val remoteActivityHelper = RemoteActivityHelper(context)
                    remoteActivityHelper.startRemoteActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            data = url.toUri()
                            addCategory(Intent.CATEGORY_BROWSABLE)
                        },
                        nodeId
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        openDialog(KEY_DIALOG_SHOW_CONTINUE_ON_PHONE)
                        loading = false
                    }
                }
            }
        }
    }

    fun openDialog(key: String){
        openDialog = key
    }

    fun closeDialog(){
        openDialog = null
    }

    companion object{
        const val KEY_DIALOG_SHOW_CONTINUE_ON_PHONE = "dialog_show_continue_on_phone"
    }
}
