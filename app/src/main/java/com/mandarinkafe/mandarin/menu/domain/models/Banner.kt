package com.mandarinkafe.mandarin.menu.domain.models

data class Banner(
    val imageUrl: String,
    val goToIdOnClick: String    // ID Товара или категории, который нужно показать юзеру при клике на баннер
)

val mockBannersList = listOf(
    Banner(
        "https://static.tildacdn.com/tild3033-3362-4338-b939-346635366637/banners_pizza.jpg",
        "9a9c0f12-123b-4d9f-8a34-cf1234abcd12"
    ),
    Banner(
        "https://static.tildacdn.com/tild6135-6436-4033-b364-323832663837/banners_pizza3.jpg",
        "j3872541-5a16-444448912555f9090d36"
    ),
    Banner(
        "https://static.tildacdn.com/tild3963-3836-4661-a430-643139333338/banners_pizza5.jpg",
        "12355555d3872541-5a16-4c21-b9e7-c8ab8912fd36"
    ),
    Banner(
        "https://static.tildacdn.com/tild3936-6238-4961-a530-373231396438/banners_pizza7.jpg",
        "23239a9c0f12-123b-4d9f-8a34-cf1234abcd12"
    )
)