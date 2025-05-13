package com.mandarinkafe.mandarin.features.menu.domain.models

data class Banner(
    val imageUrl: String,
    val goToNameOnClick: String    // Название Товара или категории, который нужно показать юзеру при клике на баннер
)

val mockBannersList = listOf(
    Banner(
        "https://static.tildacdn.com/tild6136-6464-4962-b235-613030643664/_2025-03-14_14254371.png",
        "Черный бархат"
    ),
    Banner(
        "https://static.tildacdn.com/tild6163-6130-4162-b530-376235373339/_page-0001_1.jpg",
        "Кимпабы"
    ),
    Banner(
        "https://static.tildacdn.com/tild6462-3037-4735-b935-643964393966/_page-0001.jpg",
        "Аранчини"
    ),
)