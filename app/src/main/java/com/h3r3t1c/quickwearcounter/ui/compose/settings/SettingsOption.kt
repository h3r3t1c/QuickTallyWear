package com.h3r3t1c.quickwearcounter.ui.compose.settings

import androidx.compose.ui.graphics.Color

sealed class SettingsOption(val key: String) {

    class SectionHeader(val titleResource: Int): SettingsOption(titleResource.toString())
    class ColorOption(val titleResource: Int, dataStorePrefsKey: String): SettingsOption(dataStorePrefsKey)
    class InfoOption(val iconResource: Int, val titleResource: Int, val value: String): SettingsOption(titleResource.toString())
    class ClickOption(val iconResource: Int, val titleResource: Int, val subtitleResource: Int?, val onClick: () -> Unit): SettingsOption(titleResource.toString())
    class AppOption(val iconResource: Int, val iconBackgroundColor: Color, val titleResource: Int, val onClick: () -> Unit): SettingsOption(titleResource.toString())
    class SwitchOption(val titleResource: Int, val subTitleResource: Int?, dataStorePrefsKey: String): SettingsOption(dataStorePrefsKey)
    class NavDestinationOption(val titleResource: Int, val subtitleResource: Int?, val destination: String): SettingsOption(destination)
}