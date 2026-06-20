package com.h3r3t1c.quickwearcounter.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.h3r3t1c.quickwearcounter.ext.dataStore
import kotlinx.coroutines.flow.first

object DataStorePrefs {
    const val KEY_APP_THEME_COLOR = "app_theme_color"
    const val KEY_CURRENT_COUNT = "current_count"
    const val KEY_KEEP_SCREEN_ON = "keep_screen_on"

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

    suspend fun updateBoolean(context: Context, key: String, value: Boolean) {
        context.dataStore.edit { settings ->
            settings[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun getCurrentCount(context: Context): Int? {
        return context.dataStore.data.first()[intPreferencesKey(KEY_CURRENT_COUNT)]
    }
    suspend fun updateCurrentCount(context: Context, value: Int) {
        updateInt(context, KEY_CURRENT_COUNT, value)
    }
    suspend fun updateCountByAmount(context: Context, amount: Int): Int {
        val currentCount = getCurrentCount(context) ?: 0
        val newCount = currentCount + amount
        updateCurrentCount(context, newCount)
        return newCount
    }
    suspend fun getAppThemeColor(context: Context): Int? {
        return context.dataStore.data.first()[intPreferencesKey(KEY_APP_THEME_COLOR)]
    }
}