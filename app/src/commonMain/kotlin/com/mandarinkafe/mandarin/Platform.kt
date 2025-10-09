package com.mandarinkafe.mandarin

expect class Platform() {
    val name: String
}

fun getPlatform(): Platform = Platform()
