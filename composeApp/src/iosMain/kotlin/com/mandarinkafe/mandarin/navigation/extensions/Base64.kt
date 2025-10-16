package com.mandarinkafe.mandarin.navigation.extensions


actual object Base64 {
    actual fun encodeToString(data: ByteArray, flags: Int): String {
        // Simple Base64 implementation for iOS
        val base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val result = StringBuilder()
        
        var i = 0
        while (i < data.size) {
            val a = data[i].toInt() and 0xFF
            val b = if (i + 1 < data.size) data[i + 1].toInt() and 0xFF else 0
            val c = if (i + 2 < data.size) data[i + 2].toInt() and 0xFF else 0
            
            val bitmap = (a shl 16) or (b shl 8) or c
            
            result.append(base64Chars[(bitmap shr 18) and 63])
            result.append(base64Chars[(bitmap shr 12) and 63])
            result.append(if (i + 1 < data.size) base64Chars[(bitmap shr 6) and 63] else '=')
            result.append(if (i + 2 < data.size) base64Chars[bitmap and 63] else '=')
            
            i += 3
        }
        
        return result.toString()
    }
    
    actual fun decode(str: String, flags: Int): ByteArray {
        // Simple Base64 decode implementation for iOS
        val base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val result = mutableListOf<Byte>()
        
        var i = 0
        while (i < str.length) {
            val a = if (i < str.length) base64Chars.indexOf(str[i]) else 0
            val b = if (i + 1 < str.length) base64Chars.indexOf(str[i + 1]) else 0
            val c = if (i + 2 < str.length) base64Chars.indexOf(str[i + 2]) else 0
            val d = if (i + 3 < str.length) base64Chars.indexOf(str[i + 3]) else 0
            
            if (a != -1 && b != -1) {
                val bitmap = (a shl 18) or (b shl 12) or (c shl 6) or d
                
                result.add(((bitmap shr 16) and 0xFF).toByte())
                if (str[i + 2] != '=') {
                    result.add(((bitmap shr 8) and 0xFF).toByte())
                }
                if (str[i + 3] != '=') {
                    result.add((bitmap and 0xFF).toByte())
                }
            }
            
            i += 4
        }
        
        return result.toTypedArray().map { it.toByte() }.toByteArray()
    }
}

actual object Base64Constants {
    actual val URL_SAFE: Int = 2
    actual val NO_WRAP: Int = 4
}

actual object URLEncoder {
    actual fun encode(str: String, charset: String): String {
        // Simple URL encoding for iOS
        return str.map { char ->
            when {
                char.isLetterOrDigit() || char == '-' || char == '_' || char == '.' || char == '~' -> char.toString()
                char == ' ' -> "+"
                else -> "%${char.code.toString(16).uppercase()}"
            }
        }.joinToString("")
    }
}
