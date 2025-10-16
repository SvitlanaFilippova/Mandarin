package com.mandarinkafe.mandarin.navigation.extensions

import android.util.Base64
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

actual object Base64 {
    actual fun encodeToString(data: ByteArray, flags: Int): String {
        return android.util.Base64.encodeToString(data, flags)
    }
    
    actual fun decode(str: String, flags: Int): ByteArray {
        return android.util.Base64.decode(str, flags)
    }
}

actual object Base64Constants {
    actual val URL_SAFE: Int = android.util.Base64.URL_SAFE
    actual val NO_WRAP: Int = android.util.Base64.NO_WRAP
}

actual object URLEncoder {
    actual fun encode(str: String, charset: String): String {
        return URLEncoder.encode(str, charset)
    }
}
