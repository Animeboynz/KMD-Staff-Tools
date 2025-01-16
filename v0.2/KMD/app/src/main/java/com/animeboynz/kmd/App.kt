package com.animeboynz.kmd

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.animeboynz.kmd.di.DatabaseModule
import com.animeboynz.kmd.di.FileManagerModule
import com.animeboynz.kmd.di.NetworkModule
import com.animeboynz.kmd.di.PreferencesModule
import com.animeboynz.kmd.di.RepositoryModule
import com.animeboynz.kmd.di.SerializationModule
import com.animeboynz.kmd.presentation.crash.CrashActivity
import com.animeboynz.kmd.presentation.crash.GlobalExceptionHandler

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(applicationContext, CrashActivity::class.java),
        )

        startKoin {
            androidContext(this@App)

            modules(
                FileManagerModule,
                NetworkModule,
                PreferencesModule,
                RepositoryModule,
                SerializationModule,
                DatabaseModule,
            )
        }
    }
}