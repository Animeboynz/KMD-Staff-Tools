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

suspend fun fetchImageUrls(sku: String): List<String> {
    var stockImageURL = "https://app.kathmandu.co.nz/graphql?query=query+productDetailBySku%28%24sku%3AString%29%7Bproducts%28filter%3A%7Bsku%3A%7Beq%3A%24sku%7D%7D%29%7Bitems%7B__typename+id+name+sku+url_key+...on+ConfigurableProduct%7Bconfigurable_options%7Battribute_code+attribute_id+id+label+values%7Bdefault_label+label+uid+store_label+use_default_value+value_index+swatch_data%7B...on+ImageSwatchData%7Bthumbnail+__typename%7Dvalue+__typename%7D__typename%7D__typename%7Dvariants%7Battributes%7Bcode+value_index+__typename%7Dproduct%7Bbrand_label+groupPriceTiers%7Bcustomer_group+value+__typename%7Did+kmd_product_label%7Bname+__typename%7Dmedia_gallery_entries%7Bid+disabled+file+label+position+__typename%7Dprice_range%7Bminimum_price%7Bfinal_price%7Bvalue+currency+__typename%7Dregular_price%7Bvalue+currency+__typename%7D__typename%7D__typename%7Dsku+special_price+stock_status+wasPrice%7Bvalue+currency+__typename%7D__typename%7D__typename%7D__typename%7D%7D__typename%7D%7D&operationName=productDetailBySku&variables=%7B%22sku%22%3A%22${sku}%2F%22%7D"
    var stockCLRImageURL = "https://app.kathmandu.co.nz/graphql?query=query+productDetailBySku%28%24sku%3AString%29%7Bproducts%28filter%3A%7Bsku%3A%7Beq%3A%24sku%7D%7D%29%7Bitems%7B__typename+id+name+sku+url_key+...on+ConfigurableProduct%7Bconfigurable_options%7Battribute_code+attribute_id+id+label+values%7Bdefault_label+label+uid+store_label+use_default_value+value_index+swatch_data%7B...on+ImageSwatchData%7Bthumbnail+__typename%7Dvalue+__typename%7D__typename%7D__typename%7Dvariants%7Battributes%7Bcode+value_index+__typename%7Dproduct%7Bbrand_label+groupPriceTiers%7Bcustomer_group+value+__typename%7Did+kmd_product_label%7Bname+__typename%7Dmedia_gallery_entries%7Bid+disabled+file+label+position+__typename%7Dprice_range%7Bminimum_price%7Bfinal_price%7Bvalue+currency+__typename%7Dregular_price%7Bvalue+currency+__typename%7D__typename%7D__typename%7Dsku+special_price+stock_status+wasPrice%7Bvalue+currency+__typename%7D__typename%7D__typename%7D__typename%7D%7D__typename%7D%7D&operationName=productDetailBySku&variables=%7B%22sku%22%3A%22${sku}-CLR%2F%22%7D"

    return withContext(Dispatchers.IO) {
        try {
            val imageUrls = linkedSetOf<String>() // Keeps order & removes duplicates across both URLs
            imageUrls.addAll(fetchAndParseImages(stockImageURL))
            imageUrls.addAll(fetchAndParseImages(stockCLRImageURL))
            imageUrls.toList() // Convert back to list
        } catch (e: Exception) {
            Log.e("ImageFetch", "Error fetching images", e)
            emptyList()
        }
    }
}

private fun fetchAndParseImages(url: String): List<String> {
    return try {
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        parseImageJson(response)
    } catch (e: Exception) {
        Log.e("ImageFetch", "Error fetching images from $url", e)
        emptyList()
    }
}

fun parseImageJson(jsonString: String): List<String> {
    return try {
        val jsonObject = JSONObject(jsonString)
        val itemsArray = jsonObject.getJSONObject("data")
            .getJSONObject("products")
            .getJSONArray("items")

        val imageUrls = mutableListOf<String>() // Preserve order for a single request
        for (i in 0 until itemsArray.length()) {
            val variantsArray = itemsArray.getJSONObject(i).getJSONArray("variants")
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
        imageUrls
    } catch (e: Exception) {
        Log.e("ImageParsing", "Error parsing image JSON", e)
        emptyList()
    }
}
