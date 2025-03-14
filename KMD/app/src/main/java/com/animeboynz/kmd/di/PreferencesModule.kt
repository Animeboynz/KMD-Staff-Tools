package com.animeboynz.kmd.di

import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.preferences.GeneralPreferences
import com.animeboynz.kmd.preferences.preference.AndroidPreferenceStore
import com.animeboynz.kmd.preferences.preference.PreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val PreferencesModule = module {
    single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)

    singleOf(::AppearancePreferences)
    singleOf(::GeneralPreferences)
}
