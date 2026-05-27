package com.h3r3t1c.quickwearcounter.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.h3r3t1c.quickwearcounter.ext.dataStore

object DataStorePrefs {
    const val KEY_APP_THEME_COLOR = "app_theme_color"
    const val KEY_CURRENT_COUNT = "current_count"

    //val appThemeColor = intPreferencesKey(KEY_APP_THEME_COLOR)

    suspend fun updateInt(context: Context, key: String, value: Int?) {
        if (value == null){
            context.dataStore.edit { settings ->
                settings.remove(intPreferencesKey(key))
            }
        }else {
            context.dataStore.edit { settings ->
                settings[intPreferencesKey(key)] = value
            }
        }
    }
}