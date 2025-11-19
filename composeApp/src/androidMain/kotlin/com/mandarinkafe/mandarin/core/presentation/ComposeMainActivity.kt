package com.mandarinkafe.mandarin.core.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.mandarinkafe.mandarin.features.payment.YooKassaActivityHelper
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentContract.PaymentEvent
import com.mandarinkafe.mandarin.features.payment.presentation.viewmodel.PaymentViewModel
import com.mandarinkafe.mandarin.kmp.MainScreen
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.imageBitmapDecoder
import io.kamel.image.config.imageVectorDecoder
import io.kamel.image.config.svgDecoder
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import org.koin.mp.KoinPlatform.getKoin
import java.io.File

class ComposeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Регистрируем Activity для работы с YooKassa платежами
        YooKassaActivityHelper.registerActivity(this)

        // Обрабатываем deep link при запуске приложения
        handleDeepLink(intent)

        val kamelConfig = initKamel()

        setContent {
            CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
                MainScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Устанавливаем новый intent для правильной обработки deep link
        setIntent(intent)
        // Обрабатываем deep link, когда приложение уже запущено
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "mandarin" && data.host == "payment" && data.path == "/return") {
            // Извлекаем order_id из query параметров
            val orderId = data.getQueryParameter("order_id")
            if (orderId != null) {
                // Получаем PaymentViewModel через Koin и отправляем событие для обработки возврата
                try {
                    val paymentViewModel: PaymentViewModel = getKoin().get()
                    paymentViewModel.onEvent(PaymentEvent.HandleReturnFromBrowser)
                } catch (e: Exception) {
                    // Если ViewModel еще не создан, это нормально - polling запустится позже
                }
            }
        }
    }

    // настраиваем Kamel, чтобы работало кэширование изображений
    private fun initKamel() = KamelConfig {
        takeFrom(KamelConfig.Default)

        // memory cache
        imageBitmapCacheSize = IMAGE_BITMAP_CACHE_ENTRIES
        imageVectorCacheSize = IMAGE_VECTOR_CACHE_ENTRIES
        svgCacheSize = SVG_CACHE_ENTRIES

        // декодеры
        imageBitmapDecoder()
        imageVectorDecoder()
        svgDecoder()

        httpUrlFetcher {
            // дисковый кеш
            httpCache(HTTP_CACHE_SIZE_BYTES)

            // устанавливаем реальное файловое хранилище для кеша
            install(HttpCache) {
                publicStorage(FileStorage(File(cacheDir, KAMEL_CACHE_DIR)))
            }

            defaultRequest {
                headers.append(
                    HttpHeaders.CacheControl,
                    "max-age=${HTTP_CACHE_MAX_AGE_SECONDS}"
                )
            }
            //  логирование Ktor для диагностики
            Logging {
                level = LogLevel.INFO
                logger = Logger.SIMPLE
            }
        }
    }

    private companion object Companion {
        private const val KAMEL_CACHE_DIR = "kamel_cache"
        private const val HTTP_CACHE_SIZE_BYTES = 50L * 1024L * 1024L // 50 Мб
        private const val HTTP_CACHE_MAX_AGE_SECONDS = 24 * 60 * 60 // 1 день
        private const val IMAGE_BITMAP_CACHE_ENTRIES = 200
        private const val IMAGE_VECTOR_CACHE_ENTRIES = 200
        private const val SVG_CACHE_ENTRIES = 200
    }
}
