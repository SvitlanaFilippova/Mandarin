package com.mandarinkafe.mandarin.navigation.bottomnav

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.navigation.NavConstants

sealed class BottomNavigationItem(@StringRes val title: Int, val icon: Int, val route: String) {
    object Other : BottomNavigationItem(
        R.string.more_options,
        R.drawable.ic_more,
        NavConstants.MORE_MENU_SCREEN_ROUTE
    )

    object Menu : BottomNavigationItem(
        R.string.menu,
        R.drawable.ic_food,
        NavConstants.MENU_SCREEN_ROUTE
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