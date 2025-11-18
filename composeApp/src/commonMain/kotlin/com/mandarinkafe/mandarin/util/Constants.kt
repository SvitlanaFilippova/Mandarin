package com.mandarinkafe.mandarin.util

object Constants {
    // Network constants
    const val NO_CONNECTION = -1
    const val HTTP_SUCCESS = 200
    const val HTTP_400 = 400
    const val HTTP_NOT_FOUND = 404
    const val HTTP_SERVER_ERROR = 500
    const val IMAGE_VALIDATOR_TIMEOUT = 1000
    const val MENU_WAIT_TIMEOUT = 10000L
    const val USER_DATA_WAIT_TIMEOUT = 5000L
    const val BEARER_TOKEN_TYPE = "Bearer"
    const val HEADER_API_KEY = "x-api-key"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val ERROR_BODY_READ_FAILED = "не удалось прочитать тело ответа: "

    // Menu UI
    const val BANNERS_AUTO_SCROLL_INTERVAL: Long = 5000L
    const val BANNERS_ASPECT_RATIO = 2.91f
    const val FORCE_SHOW_FAB_DURATION_MS = 3000L
    const val DEFAULT_UNSELECTED_INDEX = -1
    const val DEFAULT_SELECTED_FIRST_INDEX = 0
    const val MAX_LINES_FOR_LABEL_FLOW_ROW = 3
    const val MAX_LINES_FOR_LABEL_CHIP = 1
    const val MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU = 4
    const val MAX_LINES_FOR_MEAL_TITLE_IN_MENU = 3
    const val MAX_LINES_FOR_COLLAPSED_EXPANDABLE_TEXT = 4
    const val MIN_LINES_FOR_ADDRESS_INPUT = 2
    const val MENU_IMAGE_SPACING_COUNT = 3
    const val MENU_IMAGE_COLUMN_COUNT = 2

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
    const val MIN_MODIFIER_LIMIT_TO_IGNORE = 50

    // common UI
    const val ANIMATION_DURATION_SLOW = 1000
    const val ANIMATION_DURATION_FAST = 300
    const val GRADIENT_HEIGHT_RATIO = 0.5f
    const val ALPHA_50 = 0.5f
    const val TITLE_FONT_WEIGHT = 800
    const val NON_BRAKING_SPACE = "\u00A0"
    const val RATIO_FOR_IMAGE_CROP_MIN = 0.75f
    const val RATIO_FOR_IMAGE_CROP_MAX = 1.5f
    const val DELAY_FOR_UI_RENDERING = 300L

    // Order
    const val VALID_PHONE_LENGTH = 10
    const val DEFAULT_SAVED_ADDRESSES_NUMBER = 3
    const val SHOULD_REFRESH_ADDRESSES_KEY = "shouldRefreshAddresses"
    const val SHOULD_SELECT_ADDRESS_ID = "should_select_last_added"
    const val PAYMENT_ONLINE_CODE = "CARD"
    const val PAYMENT_BANK_CODE = "BANK"
    const val PAYMENT_CASH_CODE = "CASH"


    // Теги для особой логики в меню
    const val TAG_NO_ADDS = "NO_ADDS"
    const val TAG_NO_DISCOUNT = "NO_DISCOUNT"
    const val TAG_NO_DELIVERY = "NO_DELIVERY"
    const val TAG_IS_DELIVERY_POSITION = "IS_DELIVERY_POSITION"
    const val TAG_PIZZERIA = "PIZZERIA"
    const val TAG_CAFE = "CAFE"
    const val LABEL_18 = "18+"

    const val CATEGORY_ADDS = "Добавки"

    // remoteConfig
    const val VEG_LABEL_DEFAULT = "veg"
    const val HOT_LABEL_DEFAULT = "hot"
    const val NEW_LABEL_DEFAULT = "new"
    const val HIT_LABEL_DEFAULT = "hit"
    const val SALE_LABEL_DEFAULT = "sale"

    const val LOCALE_RU = "ru_RU"

    const val MANDARIN_PHONE = "+7(495)108–70–55"
    const val PHONE_PREFIX_RU = "+7"
    const val SMS_CODE_LENGTH = 6
    const val SECONDS_TO_CALL_DEFAULT = 300
    const val SECONDS_TO_RESEND_SMS_DEFAULT = 60
    const val DELAY_1_SECOND = 1000L

}

