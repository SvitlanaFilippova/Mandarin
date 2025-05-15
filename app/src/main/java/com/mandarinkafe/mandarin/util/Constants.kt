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
    const val MAX_LINES_FOR_FLOW_ROW = 3
    const val MAX_LINES_FOR_LABEL_CHIP = 1
    const val MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT = 6

    // Menu loading
    const val MAX_ATTEMPTS = 3
    const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 100L

    // DI
    const val IIKO_BASE_URL = "https://api-ru.iiko.services"
    const val LOCAL_STORAGE_NAME = "local_storage"

    // API
    const val BEARER_PREFIX = "Bearer "

    // Внутренние теги для особой логики в меню
    const val TAG_PIZZA_ADDS = "добавки к пицце"
    const val TAG_WOK_CONSTRUCTOR = "конструктор вок"

    // Для обработки особых категорий по их названиям
    const val CATEGORY_PIZZA_ADDS = "добавки"
    const val CATEGORY_RECOMMENDS = "рекоменд"

    // DEBOUNCE для корзины
    const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
    const val CLEAR_CART_DEBOUNCE_DELAY: Long = 3000L
    const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    const val UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE: Long = 500L

    // Стоимость доставки
    const val DELIVERY_PRICE_AREA1 = 150
    const val DELIVERY_PRICE_AREA2 = 200
    const val DELIVERY_PRICE_AREA3 = 300
    const val DELIVERY_PRICE_AREA4 = 350
    const val DELIVERY_PRICE_AREA5 = 400
    const val DELIVERY_PRICE_AREA6 = 450
    const val DELIVERY_PRICE_AREA7 = 550
    const val DELIVERY_PRICE_AREA8 = 650
    const val DELIVERY_PRICE_AREA9 = 750
    const val DELIVERY_PRICE_AREA10 = 850
    const val DELIVERY_PRICE_AREA11 = 950
    const val DELIVERY_PRICE_AREA12 = 1050
    const val DELIVERY_PRICE_FREE_AREA1 = 700
    const val DELIVERY_PRICE_FREE_AREA2 = 800
    const val DELIVERY_PRICE_FREE_AREA3 = 1500
    const val DELIVERY_PRICE_FREE_AREA4 = 2000
    const val DELIVERY_PRICE_FREE_AREA5 = 2200
    const val DELIVERY_PRICE_FREE_AREA6 = 2500
    const val DELIVERY_PRICE_FREE_AREA7 = 3000
    const val DELIVERY_PRICE_FREE_AREA8 = 3200
    const val DELIVERY_PRICE_FREE_AREA9 = 4000
    const val DELIVERY_PRICE_FREE_AREA10 = 4500
    const val DELIVERY_PRICE_FREE_AREA11 = 5000
    const val DELIVERY_PRICE_FREE_AREA12 = 5500

    // remoteConfig
    const val PHONE_NUMBER_DEFAULT = "tel:+74951087055"
    const val PHONE_NUMBER_KEY = "phone_number_key"
    const val VEG_LABEL_DEFAULT = "veg"
    const val VEG_LABEL_KEY = "veg_label_key"
    const val HOT_LABEL_DEFAULT = "hot"
    const val HOT_LABEL_KEY = "hot_label_key"
    const val NEW_LABEL_DEFAULT = "new"
    const val NEW_LABEL_KEY = "new_label_key"
    const val HIT_LABEL_DEFAULT = "hit"
    const val HIT_LABEL_KEY = "hit_label_key"
}
