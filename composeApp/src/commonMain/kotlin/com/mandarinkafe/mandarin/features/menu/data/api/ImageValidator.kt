package com.mandarinkafe.mandarin.features.menu.data.api

interface ImageValidator {
    suspend fun isImageUrlValid(url: String): Boolean
}