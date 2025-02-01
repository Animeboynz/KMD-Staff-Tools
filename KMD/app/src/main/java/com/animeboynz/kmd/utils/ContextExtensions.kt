package com.animeboynz.kmd.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.animeboynz.kmd.R

/**
 * Copies a string to clipboard
 *
 * @param label Label to show to the user describing the content
 * @param content the actual text to copy to the board
 */
fun Context.copyToClipboard(label: String, content: String) {
    if (content.isBlank()) return

    try {
        val clipboard = getSystemService<ClipboardManager>()!!
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))

        // Android 13 and higher shows a visual confirmation of copied contents
        // https://developer.android.com/about/versions/13/features/copy-paste
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast(getString(R.string.copied_to_clipboard))
        }
    } catch (e: Throwable) {
        toast(getString(R.string.clipboard_copy_error))
    }
}

fun Context.openInBrowser(url: String, forceDefaultBrowser: Boolean = false) {
    this.openInBrowser(url.toUri(), forceDefaultBrowser)
}

fun Context.openInBrowser(uri: Uri, forceDefaultBrowser: Boolean = false) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            // Force default browser so that verified extensions don't re-open Tachiyomi
            if (forceDefaultBrowser) {
                defaultBrowserPackageName()?.let { setPackage(it) }
            }
        }
        startActivity(intent)
    } catch (e: Exception) {
        toast(e.message)
    }
}
private fun Context.defaultBrowserPackageName(): String? {
    val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
    val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.resolveActivity(
            browserIntent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
        )
    } else {
        packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
    }
    return resolveInfo
        ?.activityInfo?.packageName
        ?.takeUnless { it in DeviceUtil.invalidDefaultBrowsers }
}
