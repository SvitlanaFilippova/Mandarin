package com.mandarinkafe.mandarin.util

import io.github.aakira.napier.Napier

object AppLog {

    private fun getAutoTag(): String {
        val stackTrace = Throwable().stackTrace
        return stackTrace.getOrNull(2)?.let { element ->
            element.fileName?.substringBefore(".") ?: element.className.substringAfterLast('.')
        } ?: "AppLog"
    }

    fun d(message: String) = Napier.d(message, tag = getAutoTag())

    fun i(message: String) = Napier.i(message, tag = getAutoTag())

    fun w(message: String) = Napier.w(message, tag = getAutoTag())

    fun e(message: String) = Napier.e(message, tag = getAutoTag())

    fun e(message: String, throwable: Throwable) =
        Napier.e(message, throwable, tag = getAutoTag())

}