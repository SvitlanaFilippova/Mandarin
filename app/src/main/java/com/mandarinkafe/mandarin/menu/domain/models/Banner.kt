package com.mandarinkafe.mandarin.menu.domain.models

data class Banner(
    val imageUrl: String,
    val categoryOnClick: String    // Обработчик клика
)

val mockBannersList = listOf(
    Banner(
        "https://static.tildacdn.com/tild3033-3362-4338-b939-346635366637/banners_pizza.jpg",
        "pizza"
    ),
    Banner(
        "https://static.tildacdn.com/tild6135-6436-4033-b364-323832663837/banners_pizza3.jpg",
        "pizza"
    ),
    Banner(
        "https://static.tildacdn.com/tild3963-3836-4661-a430-643139333338/banners_pizza5.jpg",
        ""
    ),
    Banner(
        "https://static.tildacdn.com/tild3936-6238-4961-a530-373231396438/banners_pizza7.jpg",
        ""
    )
)