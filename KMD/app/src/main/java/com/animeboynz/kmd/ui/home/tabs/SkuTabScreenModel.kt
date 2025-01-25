package com.animeboynz.kmd.ui.home.tabs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.asState

class SkuTabScreenModel(
    private val preferences: GeneralPreferences,
) : ScreenModel {

}
