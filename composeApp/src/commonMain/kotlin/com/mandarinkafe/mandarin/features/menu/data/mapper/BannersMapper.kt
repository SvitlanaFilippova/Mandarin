package com.mandarinkafe.mandarin.features.menu.data.mapper

import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.shared.BuildKonfig

fun BannerDto.toDomain() = Banner(
    imageUrl = buildImageUrl(imageUrl ?: ""),
    targetName = targetName ?: "",
)

private fun buildImageUrl(url: String): String {
    return if (url.startsWith("http")) {
        // Абсолютный URL - оставляем как есть
        url
    } else {
        // Относительный URL - добавляем базовый URL
        BuildKonfig.SERVER_BASE_URL + url.removePrefix("/")
    }
}


