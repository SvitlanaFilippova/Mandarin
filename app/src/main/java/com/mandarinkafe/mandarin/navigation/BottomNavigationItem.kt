package com.mandarinkafe.mandarin.navigation

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.navigation.NavRoutes.CART_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE

sealed class BottomNavigationItem(@StringRes val title: Int, val icon: Int, val route: String) {
    object Menu : BottomNavigationItem(R.string.menu, R.drawable.ic_food, MENU_SCREEN_ROUTE)
    object Delivery :
        BottomNavigationItem(R.string.delivery, R.drawable.ic_delivery, DELIVERY_SCREEN_ROUTE)
    object Favorites :
        BottomNavigationItem(
            R.string.favorite,
            R.drawable.ic_favorite_inactive,
            FAVORITES_SCREEN_ROUTE
        )
    object Cart :
        BottomNavigationItem(
            R.string.cart,
            R.drawable.ic_cart,
            CART_SCREEN_ROUTE
        )
    object Search :
        BottomNavigationItem(
            R.string.search,
            R.drawable.ic_search,
            NavRoutes.SEARCH_SCREEN_ROUTE
        )
}