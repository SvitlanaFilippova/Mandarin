package com.mandarinkafe.mandarin.features.favorites.data.models

data class Favorites(
    val items: Set<StoredFavoriteMeal>,
    val lastUpdated: Long = 0L // время последнего изменения всего избранного (задаётся на сервере, клиент только сравнивает для логики мержа)
)


