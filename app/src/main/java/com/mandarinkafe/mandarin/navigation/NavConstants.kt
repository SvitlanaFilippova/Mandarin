package com.mandarinkafe.mandarin.navigation

object NavConstants {
    // Keys for args
    const val KEY_MEAL_JSON = "mealJson"
    const val KEY_IS_EDIT_MODE = "isEditMode"
    const val KEY_ADDRESS_JSON = "addressJson"
    const val KEY_FOCUS_INPUT = "focusInput"
    const val MAIN_GRAPH = "main_graph"
    const val KEY_ORDER_ID = "orderId"
    const val KEY_REQUIRE_CONFIRMATION = "requireConfirmation"
    const val KEY_BACK_TARGET = "backTarget"

    // Screen routes
    const val MENU_SCREEN_ROUTE = "menu"
    const val FAVORITES_SCREEN_ROUTE = "favorites"
    const val CART_SCREEN_ROUTE = "cart"
    const val SPLASH_SCREEN_ROUTE = "splash"
    const val ORDER_SCREEN_ROUTE = "order"
    const val MORE_MENU_SCREEN_ROUTE = "more"
    const val ORDERS_HISTORY_ROUTE = "orders_history"
    const val SAVED_ADDRESSES_ROUTE = "saved_addresses"
    const val ABOUT_SCREEN_ROUTE = "about_screen"


    const val ADDRESS_SCREEN_ROUTE = "address"
    const val ADDRESS_SCREEN_ROUTE_WITH_ARGS =
        "$ADDRESS_SCREEN_ROUTE/{$KEY_ADDRESS_JSON}"

    const val ADDRESS_DETAILS_ROUTE = "address_details"
    const val ADDRESS_DETAILS_ROUTE_WITH_ARGS =
        "$ADDRESS_DETAILS_ROUTE/{$KEY_ADDRESS_JSON}/{$KEY_IS_EDIT_MODE}/{$KEY_BACK_TARGET}"

    const val MEAL_DETAILS_ROUTE = "meal_details"
    const val MEAL_DETAILS_ROUTE_WITH_ARGS =
        "$MEAL_DETAILS_ROUTE/{$KEY_MEAL_JSON}/{$KEY_IS_EDIT_MODE}"

    const val SEARCH_SCREEN_ROUTE = "search"
    const val SEARCH_SCREEN_ROUTE_WITH_ARGS =
        "$SEARCH_SCREEN_ROUTE?$KEY_FOCUS_INPUT={$KEY_FOCUS_INPUT}"

    const val ORDER_INFO_ROUTE = "order_info"
    const val ORDER_INFO_ROUTE_WITH_ARGS =
        "$ORDER_INFO_ROUTE/{$KEY_ORDER_ID}/{$KEY_REQUIRE_CONFIRMATION}"

    val RoutesWithBackButton = setOf(
        ADDRESS_SCREEN_ROUTE,
        ADDRESS_SCREEN_ROUTE_WITH_ARGS,
        ADDRESS_DETAILS_ROUTE_WITH_ARGS,
        ORDER_SCREEN_ROUTE,
        ORDERS_HISTORY_ROUTE,
        SAVED_ADDRESSES_ROUTE,
        ABOUT_SCREEN_ROUTE
    )
}