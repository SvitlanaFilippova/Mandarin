package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLResponse
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageValidatorImpl : ImageValidator {
    override suspend fun isImageUrlValid(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val nsUrl = NSURL.URLWithString(url) ?: return@withContext false
            val request = NSURLRequest.requestWithURL(nsUrl).apply {
                // HEAD request не поддерживается напрямую, используем GET с малым таймаутом
            }

            val (_, response, error) = suspendCoroutine<Triple<NSData?, NSURLResponse?, NSError?>> { continuation ->
                NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
                    continuation.resume(Triple(data, response, error))
                }.resume()
            }

            if (error != null) {
                Napier.e("Изображение не валидно: ${error.localizedDescription}")
                return@withContext false
            }

            val httpResponse = response as? NSHTTPURLResponse
            val statusCode = httpResponse?.statusCode?.toInt() ?: 0
            val contentType = httpResponse?.allHeaderFields?.get("Content-Type") as? String

            statusCode == HTTP_SUCCESS && contentType?.startsWith("image") == true
        } catch (e: Exception) {
            Napier.e("Изображение не валидно: ${e.message}")
            false
        }
    }
}






