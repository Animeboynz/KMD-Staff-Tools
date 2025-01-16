package com.animeboynz.kmd.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import com.animeboynz.kmd.preferences.preference.AndroidPreferenceStore
import com.animeboynz.kmd.preferences.preference.PreferenceStore

val RepositoryModule = module {
    single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)
}
