package com.mandarinkafe.mandarin.util

object Constants {
    // Network constants
    const val HTTP_SUCCESS = 200
    const val HTTP_NOT_FOUND = 404
    const val HTTP_BAD_REQUEST = 400
    const val HTTP_SERVER_ERROR = 500

    // Menu UI
    const val AUTO_SCROLL_INTERVAL: Long = 5000L
    const val ANIMATION_DURATION_SLOW = 1000
    const val ANIMATION_DURATION_FAST = 300
    const val DEFAULT_UNSELECTED_INDEX = -1

    // Menu loading
    const val MAX_ATTEMPTS = 3
    const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 100L

    // DI
    const val IIKO_BASE_URL = "https://api-ru.iiko.services"
    const val LOCAL_STORAGE_NAME = "local_storage"

    //Navigation Routes
    const val MENU_SCREEN_ROUTE = "menu"
    const val MENU_SCOPE_ROUTE = "menu_scope"
    const val SEARCH_SCREEN_ROUTE = "search"
    const val DELIVERY_SCREEN_ROUTE = "delivery"
    const val FAVORITES_SCREEN_ROUTE = "favorites"

    //Контакты - вынести в конфиг для возможности изменений извне???
    const val PHONE_NUMBER = "tel:+74951087055"
}