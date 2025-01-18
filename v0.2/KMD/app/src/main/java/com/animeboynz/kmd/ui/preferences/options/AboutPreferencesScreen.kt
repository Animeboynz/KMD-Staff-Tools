package com.animeboynz.kmd.ui.preferences.options

import androidx.compose.runtime.Composable
import com.animeboynz.kmd.presentation.Screen

object AboutPreferencesScreen : Screen() {
    private fun readResolve(): Any = AboutPreferencesScreen

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