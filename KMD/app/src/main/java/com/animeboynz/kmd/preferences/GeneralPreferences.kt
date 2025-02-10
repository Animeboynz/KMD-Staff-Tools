package com.animeboynz.kmd.preferences

import com.animeboynz.kmd.preferences.preference.Preference
import com.animeboynz.kmd.preferences.preference.PreferenceStore

class GeneralPreferences(preferenceStore: PreferenceStore) {
    val storeName = preferenceStore.getString("store-name", "Kathmandu")
    val storeNumber = preferenceStore.getString("store-number", "000")
    val countryCode = preferenceStore.getString("country-code", "NZ")
    val orderNumberPadding = preferenceStore.getInt("order-number-padding", 4)
    val lastUsedOrderCategory = preferenceStore.getInt("last_used_order_category", 0)
    val stockCheckRegion = preferenceStore.getString("stock_check_region", "1010")
}
