package com.mandarinkafe.mandarin.features.auth.util

/**
 * iOS реализация - всегда возвращает null, так как на iOS не используется SMS Retriever API
 */
actual fun getAppSignatureHashForSms(): String? = null

