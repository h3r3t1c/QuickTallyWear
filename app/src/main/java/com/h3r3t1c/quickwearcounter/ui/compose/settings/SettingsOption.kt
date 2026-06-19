package com.h3r3t1c.quickwearcounter.ui.compose.settings

sealed class SettingsOption(val key: String) {

    class SectionHeader(val titleResource: Int): SettingsOption(titleResource.toString())
    class ColorOption(val titleResource: Int, val dataStorePrefsKey: String): SettingsOption(dataStorePrefsKey)
    class InfoOption(val titleResource: Int, val value: String): SettingsOption(titleResource.toString())
}