package com.mandarinkafe.mandarin.core.data.config

import platform.Foundation.*

actual object ApiKeys {
    private val plist: Map<Any?, *>? by lazy {
        NSBundle.mainBundle.infoDictionary
    }

    actual val MAPKIT_API_KEY: String = plist?.get("MAPKIT_API_KEY") as? String ?: ""
    actual val IIKO_API_KEY: String = plist?.get("IIKO_API_KEY") as? String ?: ""
    actual val TG_BOT_TOKEN: String = plist?.get("TG_BOT_TOKEN") as? String ?: ""
    actual val TG_CHANNEL_ID: String = plist?.get("TG_CHANNEL_ID") as? String ?: ""
    actual val DEV_TG_CHAT_ID: String = plist?.get("DEV_TG_CHAT_ID") as? String ?: ""
    actual val SERVER_BASE_URL: String = plist?.get("SERVER_BASE_URL") as? String ?: ""
    actual val MANDARIN_API_KEY: String = plist?.get("MANDARIN_API_KEY") as? String ?: ""
}