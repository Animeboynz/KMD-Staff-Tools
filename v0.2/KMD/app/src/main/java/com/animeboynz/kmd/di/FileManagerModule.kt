package com.animeboynz.kmd.di

import com.github.k1rakishou.fsaf.FileManager
import org.koin.dsl.module

val FileManagerModule = module {
    single { FileManager(get()) }
}