package com.animeboynz.kmd

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SearchScreen() {
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.userAgentString = settings.userAgentString.replace("Mobile", "eliboM").replace("Android", "diordnA")
                webViewClient = WebViewClient()
                loadUrl("https://google.com/")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // Ensure the WebView content is not under the status bar
    )
}
