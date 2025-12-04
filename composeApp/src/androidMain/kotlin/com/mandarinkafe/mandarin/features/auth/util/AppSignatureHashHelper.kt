package com.mandarinkafe.mandarin.features.auth.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import io.github.aakira.napier.Napier
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Утилита для получения хэша подписи приложения для SMS Retriever API
 *
 * - Если приложение установлено через Google Play с App Signing:
 *   → Хэш будет от Google Play App Signing ключа (совпадает с Google Play Console)
 *
 * - Если приложение установлено напрямую (APK) или через внутреннее тестирование:
 *   → Хэш будет от вашего локального keystore
 *
 * Для SMS Retriever используйте хэш, который выводится в логах этой утилитой -
 * это хэш реально установленного приложения на устройстве.
 */
object AppSignatureHashHelper {
    private const val SMS_HASH_LENGTH = 11

    /**
     * Получает хэш подписи приложения для SMS Retriever API
     *
     * @param context Android контекст
     * @return 11-символьный хэш в Base64 формате (например, "FA+9qCX9VSu")
     *         или null, если не удалось получить хэш
     */
    fun getAppSignatureHash(context: Context): String? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // API 28+ используем новый метод
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatureBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // API 28+ используем signingInfo
                packageInfo.signingInfo?.apkContentsSigners?.get(0)?.toByteArray()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures?.get(0)?.toByteArray()
            }

            if (signatureBytes == null) {
                Napier.e { "AppSignatureHash: Signature bytes are null" }
                return null
            }

            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(signatureBytes)

            // Берем первые 11 символов из Base64 хэша
            val base64Hash = Base64.encodeToString(hash, Base64.NO_PADDING or Base64.NO_WRAP)
            val smsHash = base64Hash.substring(0, minOf(SMS_HASH_LENGTH, base64Hash.length))

            Napier.i { "AppSignatureHash: App signature hash: $smsHash" }
            Napier.i { "AppSignatureHash: Full SHA-256 hash: $base64Hash" }

            smsHash
        } catch (e: PackageManager.NameNotFoundException) {
            Napier.e(e) { "AppSignatureHash: Package not found" }
            null
        } catch (e: NoSuchAlgorithmException) {
            Napier.e(e) { "AppSignatureHash: SHA-256 algorithm not found" }
            null
        } catch (e: Exception) {
            Napier.e(e) { "AppSignatureHash: Error getting app signature hash" }
            null
        }
    }
}

