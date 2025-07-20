package com.mandarinkafe.mandarin.navigation

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R

sealed class BottomNavigationItem(@StringRes val title: Int, val icon: Int, val route: String) {

    // TODO Временно, только для теста экрана оформления заказа
    object Order : BottomNavigationItem(
        R.string.submit_order,
        R.drawable.ic_edit,
        NavConstants.ORDER_SCREEN_ROUTE
    )

    object Menu : BottomNavigationItem(
        R.string.menu,
        R.drawable.ic_food,
        NavConstants.MENU_SCREEN_ROUTE
    )

    object Delivery : BottomNavigationItem(
        R.string.delivery,
        R.drawable.ic_delivery,
        NavConstants.DELIVERY_SCREEN_ROUTE
    )

    object Favorites :
        BottomNavigationItem(
            R.string.favorite,
            R.drawable.ic_favorite_inactive,
            NavConstants.FAVORITES_SCREEN_ROUTE
        )

    object Cart :
        BottomNavigationItem(
            R.string.cart,
            R.drawable.ic_cart,
            NavConstants.CART_SCREEN_ROUTE
        )

    object Search :
        BottomNavigationItem(
            R.string.search,
            R.drawable.ic_search,
            NavConstants.SEARCH_SCREEN_ROUTE
        )
}