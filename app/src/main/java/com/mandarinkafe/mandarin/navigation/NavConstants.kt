package com.mandarinkafe.mandarin.navigation

object NavConstants {
    const val MENU_SCREEN_ROUTE = "menu"
    const val DELIVERY_SCREEN_ROUTE = "delivery"
    const val FAVORITES_SCREEN_ROUTE = "favorites"
    const val CART_SCREEN_ROUTE = "cart"
    const val SPLASH_SCREEN_ROUTE = "splash"
    const val ORDER_SCREEN_ROUTE = "order"

    const val MEAL_DETAILS_ROUTE = "meal_details"
    const val KEY_MEAL_JSON = "mealJson"
    const val KEY_IS_EDIT_MODE = "isEditMode"
    const val MEAL_DETAILS_ROUTE_WITH_ARGS =
        "$MEAL_DETAILS_ROUTE/{$KEY_MEAL_JSON}/{$KEY_IS_EDIT_MODE}"

    const val SEARCH_SCREEN_ROUTE = "search"
    const val KEY_FOCUS_INPUT = "focusInput"
    const val SEARCH_SCREEN_ROUTE_WITH_ARGS =
        "$SEARCH_SCREEN_ROUTE?$KEY_FOCUS_INPUT={$KEY_FOCUS_INPUT}"

}