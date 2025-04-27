package com.mandarinkafe.mandarin.util

object Constants {
    // Network constants
    const val HTTP_SUCCESS = 200
    const val HTTP_SERVER_ERROR = 500

    // Menu UI
    const val AUTO_SCROLL_INTERVAL: Long = 5000L
    const val ANIMATION_DURATION_SLOW = 1000
    const val ANIMATION_DURATION_FAST = 300
    const val DEFAULT_UNSELECTED_INDEX = -1
    const val DEFAULT_SELECTED_FIRST_INDEX = 0

    // Menu loading
    const val MAX_ATTEMPTS = 3
    const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 100L

    // DI
    const val IIKO_BASE_URL = "https://api-ru.iiko.services"
    const val LOCAL_STORAGE_NAME = "local_storage"

    // API
    const val BEARER_PREFIX = "Bearer "

    // Контакты - вынести в конфиг для возможности изменений извне
    const val PHONE_NUMBER = "tel:+74951087055"

    // Внутренние теги для особой логики в меню
    const val TAG_PIZZA_ADDS = "добавки к пицце"
    const val TAG_WOK_CONSTRUCTOR = "конструктор вок"

    // Для обработки особых категорий по их названиям
    const val CATEGORY_PIZZA_ADDS = "добавки"
    const val CATEGORY_RECOMMENDS = "рекоменд"

    // DEBOUNCE для корзины
    const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
    const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    const val UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE: Long = 500L
}
