package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.util.AppLog
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.IMAGE_VALIDATOR_TIMEOUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ImageValidatorImpl : ImageValidator {
    override suspend fun isImageUrlValid(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = IMAGE_VALIDATOR_TIMEOUT
            connection.readTimeout = IMAGE_VALIDATOR_TIMEOUT
            connection.connect()

            val code = connection.responseCode
            val contentType = connection.contentType
            connection.disconnect()

            code == HTTP_SUCCESS && contentType?.startsWith("image") == true
        } catch (e: Exception) {
            AppLog.e("Изображение не валидно: ${e.message}")
            false
        }
    }
}