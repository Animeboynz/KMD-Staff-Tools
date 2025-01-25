package com.animeboynz.kmd.preferences

import com.animeboynz.kmd.preferences.preference.PreferenceStore

class GeneralPreferences(preferenceStore: PreferenceStore) {
    val storeName = preferenceStore.getString("store-name", "Kathmandu")
    val storeNumber = preferenceStore.getString("store-number", "000")
}
