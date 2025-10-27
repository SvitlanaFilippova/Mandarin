package com.mandarinkafe.mandarin.shared.device

expect class AppVersionProvider {
    fun getVersionName(): String
    fun getVersionCode(): String
}


