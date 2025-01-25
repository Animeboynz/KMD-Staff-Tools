package com.animeboynz.kmd

import android.app.Application
import androidx.compose.runtime.getValue
import com.animeboynz.kmd.di.DatabaseModule
import com.animeboynz.kmd.di.NetworkModule
import com.animeboynz.kmd.di.PreferencesModule
import com.animeboynz.kmd.di.RepositoryModule
import com.animeboynz.kmd.di.SerializationModule
import com.animeboynz.kmd.preferences.AppearancePreferences
import com.animeboynz.kmd.presentation.crash.CrashActivity
import com.animeboynz.kmd.presentation.crash.GlobalExceptionHandler
import com.animeboynz.kmd.ui.theme.setAppCompatDelegateThemeMode
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class App : Application() {
    private val appearancePreferences by inject<AppearancePreferences>()

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(applicationContext, CrashActivity::class.java),
        )

        startKoin {
            androidContext(this@App)

            modules(
                NetworkModule,
                PreferencesModule,
                RepositoryModule,
                SerializationModule,
                DatabaseModule,
            )
        }
        setAppCompatDelegateThemeMode(appearancePreferences.themeMode.get())
    }
}