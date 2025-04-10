package com.mandarinkafe.mandarin.navigation

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.util.Constants.DELIVERY_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.FAVORITES_SCREEN_ROUTE
import com.mandarinkafe.mandarin.util.Constants.MENU_SCREEN_ROUTE

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
}