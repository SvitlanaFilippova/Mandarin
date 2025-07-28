package com.mandarinkafe.mandarin.util

object Constants {
    // Network constants
    const val NO_CONNECTION = -1
    const val HTTP_SUCCESS = 200
    const val HTTP_SERVER_ERROR = 500
    const val IMAGE_VALIDATOR_TIMEOUT = 1000

    // Menu UI
    const val BANNERS_AUTO_SCROLL_INTERVAL: Long = 5000L
    const val BANNERS_ASPECT_RATIO = 2.91f
    const val FORCE_SHOW_FAB_DURATION_MS = 3000L
    const val DEFAULT_UNSELECTED_INDEX = -1
    const val DEFAULT_SELECTED_FIRST_INDEX = 0
    const val MAX_LINES_FOR_LABEL_FLOW_ROW = 3
    const val MAX_LINES_FOR_LABEL_CHIP = 1
    const val MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU = 5
    const val MAX_LINES_FOR_MEAL_TITLE_IN_MENU = 3
    const val MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT = 6
    const val MIN_LINES_FOR_ADDRESS_INPUT = 2
    const val IMAGE_SIZE_IN_MENU = 300
    const val MENU_IMAGE_SPACING_COUNT = 3
    const val MENU_IMAGE_COLUMN_COUNT = 2
    const val OFFSET_THRESHOLD = 8

    // Splash screen
    const val SPLASH_SCREEN_DURATION = 5000L
    const val SPLASH_APPEARING_DURATION = 1000
    const val SPLASH_ANIMATION_DURATION = 2000
    const val SPLASH_ANIMATION_DELAY_FOR_ELEMENT = 80L
    const val SPLASH_GLOBAL_ALPHA_INIT = 0.1f
    const val SPLASH_LOGO_ALPHA_INIT = 0.5f

    // MealDetails UI
    const val SCROLL_TARGET_KEY = "scrollTarget"
    const val BOUNCE_OFFSET_INIT = 40f
    const val ANIMATION_REPEAT_NUMBER = 5
    const val ANIMATION_BOUNCE_STEP = 25f
    const val ANIMATION_BOUNCE_MODIFIER = 4

    // common UI
    const val MAP_ANIMATION_DURATION = 1f
    const val ANIMATION_DURATION_SLOW = 1000
    const val ANIMATION_DURATION_FAST = 300
    const val GRADIENT_HEIGHT_RATIO = 0.5f
    const val WEIGHT_RATIO_30 = 0.3f
    const val WEIGHT_RATIO_70 = 0.7f
    const val ALPHA_50 = 0.5f
    const val TITLE_FONT_WEIGHT = 800
    const val NON_BRAKING_SPACE = "\u00A0"

    // Menu loading
    const val MAX_ATTEMPTS = 3
    const val DELAY_BEFORE_NEXT_ATTEMPT: Long = 100L

    // Order
    const val VALID_PHONE_LENGTH = 10
    const val DEFAULT_SAVED_ADDRESSES_NUMBER = 3
    const val SHOULD_REFRESH_ADDRESSES_KEY = "shouldRefreshAddresses"
    const val SHOULD_SELECT_LAST_ADDED_KEY = "should_select_last_added"

    // Map
    const val MAP_DEFAULT_ZOOM = 17.0f
    const val MAP_DEFAULT_TILT = 30.0f
    const val MAP_DEFAULT_AZIMUTH = 0.0f

    // DI
    const val IIKO_BASE_URL = "https://api-ru.iiko.services"
    const val LOCAL_STORAGE_NAME = "local_storage"
    const val GOOGLE_DOCS_BASE_URL = "https://docs.google.com/spreadsheets/d/e/"
    const val BANNERS_GOOGLE_DOCS_URL =
        "2PACX-1vQ3-6HvveASGgkJk7RppqB25IlbRSGJGvdEnN_0_XTtIKtRcR6H-R4KS0L_39ifx1cnGWRUiCA2zPQZ/pub?gid=0&single=true&output=csv"
    const val RECOMMENDATIONS_GOOGLE_DOCS_URL =
        "2PACX-1vQ3-6HvveASGgkJk7RppqB25IlbRSGJGvdEnN_0_XTtIKtRcR6H-R4KS0L_39ifx1cnGWRUiCA2zPQZ/pub?gid=1629216186&single=true&output=csv"

    // API
    const val BEARER_PREFIX = "Bearer "

    // Внутренние теги для особой логики в меню
    const val TAG_ADDS = "SHOW_ADDS"
    const val TAG_NO_ADDS = "NO_ADDS"
    const val TAG_NO_DISCOUNT = "NO_DISCOUNT"
    const val TAG_NO_DELIVERY = "NO_DELIVERY"

    // Для обработки особых категорий по их названиям
    const val CATEGORY_ADDS = "добавки"
    const val CATEGORY_RECOMMENDS = "Рекомендованные (общие)"

    // DEBOUNCE для корзины
    const val DELETE_FROM_CART_DEBOUNCE_DELAY: Long = 3000L
    const val INTERVAL_FOR_UPD_PROGRESSBAR: Long = 100L
    const val UPD_RECOMMEND_AFTER_CART_CHANGE_DEBOUNCE: Long = 500L
    const val LOCALE_RU = "ru_RU"

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
