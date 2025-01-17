package com.animeboynz.kmd.ui.preferences.options

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.animeboynz.kmd.preferences.GeneralPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataPreferencesScreenModel(
    private val preferences: GeneralPreferences,
) : ScreenModel {

}
