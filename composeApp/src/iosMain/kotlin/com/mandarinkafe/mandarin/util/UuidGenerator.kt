package com.mandarinkafe.mandarin.util

import platform.Foundation.NSUUID

actual fun generateUuid(): String {
    return NSUUID().UUIDString()
}
