package com.mandarinkafe.mandarin.navigation

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R

sealed class BottomNavigationItem(@StringRes val title: Int, val icon: Int, val route: String) {
    object Search : BottomNavigationItem(R.string.search, R.drawable.ic_search, "search")
    object Delivery : BottomNavigationItem(R.string.delivery, R.drawable.ic_delivery, "delivery")
    object Favorites :
        BottomNavigationItem(R.string.favorite, R.drawable.ic_favorite_inactive, "favorites")
}