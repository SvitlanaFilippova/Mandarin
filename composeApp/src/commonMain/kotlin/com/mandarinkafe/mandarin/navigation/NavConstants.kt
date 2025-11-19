package com.mandarinkafe.mandarin.navigation

import com.mandarinkafe.mandarin.navigation.bottomnav.BottomNavigationItem

object NavConstants {
    // --- Keys ---
    const val KEY_MEAL_JSON = "mealJson" // Deprecated, используем KEY_MEAL_ID и другие
    const val KEY_MEAL_ID = "mealId"
    const val KEY_ADDS_IDS = "addsIds"
    const val KEY_MODIFIER_IDS = "modifierIds"
    const val KEY_COMMENT = "comment"
    const val KEY_CART_ITEM_ID = "cartItemId"
    const val KEY_IS_EDIT_MODE = "isEditMode"
    const val KEY_ADDRESS_JSON = "addressJson"
    const val KEY_FOCUS_INPUT = "focusInput"
    const val KEY_ORDER_ID = "orderId"
    const val KEY_RETURN_TO_ROUTE = "returnTo"
    const val KEY_FROM_ORDER_CREATION = "fromOrderCreation"
    const val KEY_IS_ONLINE_PAYMENT = "isOnlinePayment"
    const val KEY_SNACKBAR_MESSAGE = "snackbarMessage"
    const val KEY_TARGET_ROUTE = "targetRoute"
    const val KEY_PHONE = "phone"
    const val KEY_FOR_DELETE_ACCOUNT = "forDeleteAccount"
    const val KEY_PHONE_VERIFIED = "phoneVerified"

    // --- Routes ---
    const val MENU_SCREEN_ROUTE = "menu"
    const val FAVORITES_SCREEN_ROUTE = "favorites"
    const val SPLASH_SCREEN_ROUTE = "splash"
    const val ORDER_SCREEN_ROUTE = "order"
    const val MORE_MENU_SCREEN_ROUTE = "more"
    const val ORDERS_HISTORY_ROUTE = "orders_history"
    const val SAVED_ADDRESSES_ROUTE = "saved_addresses"
    const val ACCOUNT_ROUTE = "account"
    const val ABOUT_SCREEN_ROUTE = "about_screen"
    const val LEGAL_SCREEN_ROUTE = "legal_screen"
    const val DELIVERY_SCREEN_ROUTE = "delivery_screen"
    const val CONTACTS_SCREEN_ROUTE = "contacts_screen"
    const val CART_SCREEN_ROUTE = "cart"
    const val ADDRESS_SCREEN_ROUTE = "address"
    const val ADDRESS_DETAILS_ROUTE = "address_details"
    const val MEAL_DETAILS_ROUTE = "meal_details"
    const val SEARCH_SCREEN_ROUTE = "search"
    const val ORDER_INFO_ROUTE = "order_info"
    const val AUTH_ROUTE = "auth_screen"
    const val PAYMENT_SCREEN_ROUTE = "payment"
    const val KEY_AMOUNT = "amount"
    const val KEY_USER_PHONE = "userPhone"

    val bottomNavigationRoutes = getBottomNavigationRoutesFromItems()

    /**
     * Автоматически извлекает все маршруты из BottomNavigationItem
     * Используется для синхронизации с bottomNavigationRoutes
     */
    fun getBottomNavigationRoutesFromItems(): Set<String> {
        return BottomNavigationItem.entries.map { it.route }.toSet()
    }
}
