package com.animeboynz.kmd.presentation.components.product

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import androidx.compose.foundation.lazy.items

@Composable
fun ImageCarousel(imageUrls: List<String>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(imageUrls) { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .padding(4.dp)
            )
        }
    }
}

suspend fun fetchImageUrls(url: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            parseImageJson(response)
        } catch (e: Exception) {
            Log.e("ImageFetch", "Error fetching images", e)
            emptyList()
        }
    }
}

fun parseImageJson(jsonString: String): List<String> {
    return try {
        val jsonObject = JSONObject(jsonString)
        val itemsArray = jsonObject.getJSONObject("data")
            .getJSONObject("products")
            .getJSONArray("items")

        val imageUrls = mutableSetOf<String>()
        for (i in 0 until itemsArray.length()) {
            val variantsArray = itemsArray.getJSONObject(0).getJSONArray("variants")
            for (j in 0 until variantsArray.length()) {
                val mediaArray = variantsArray.getJSONObject(j)
                    .getJSONObject("product")
                    .getJSONArray("media_gallery_entries")
                for (k in 0 until mediaArray.length()) {
                    val fileUrl = mediaArray.getJSONObject(k).getString("file")
                    imageUrls.add("https://kmd-assets.imgix.net/catalog/product$fileUrl")
                }
            }
        }
        imageUrls.toList()
    } catch (e: Exception) {
        Log.e("ImageParsing", "Error parsing image JSON", e)
        emptyList()
    }
}