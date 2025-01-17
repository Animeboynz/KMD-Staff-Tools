package com.animeboynz.kmd.ui.preferences.options

import androidx.compose.runtime.Composable
import com.animeboynz.kmd.BuildConfig
import com.animeboynz.kmd.presentation.Screen
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object AboutPreferencesScreen : Screen() {
    @Composable
    override fun Content() {
        TODO("Not yet implemented")
    }

//    internal fun getFormattedBuildTime(): String {
//        return try {
//            LocalDateTime.ofInstant(
//                Instant.parse(BuildConfig.BUILD_TIME),
//                ZoneId.systemDefault(),
//            )
//        } catch (e: Exception) {
//            BuildConfig.BUILD_TIME
//        }.toString()
//    }

}