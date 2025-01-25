package com.animeboynz.kmd.di

import com.animeboynz.kmd.preferences.preference.AndroidPreferenceStore
import com.animeboynz.kmd.preferences.preference.PreferenceStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val RepositoryModule = module {
    single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)
}
