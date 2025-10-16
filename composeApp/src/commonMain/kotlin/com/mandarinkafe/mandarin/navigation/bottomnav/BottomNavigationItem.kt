package com.mandarinkafe.mandarin.navigation.bottomnav

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.navigation.NavConstants
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

sealed class BottomNavigationItem(val title: StringResource, val icon: ImageResource, val route: String) {
    object Other : BottomNavigationItem(
        MR.strings.more_options,
        MR.images.ic_more,
        NavConstants.MORE_MENU_SCREEN_ROUTE
    )

    object Menu : BottomNavigationItem(
        MR.strings.menu,
        MR.images.ic_menu_book,
        NavConstants.MENU_SCREEN_ROUTE
    )

    object Favorites :
        BottomNavigationItem(
            MR.strings.favorite,
            MR.images.ic_favorite_inactive,
            NavConstants.FAVORITES_SCREEN_ROUTE
        )

    object Cart :
        BottomNavigationItem(
            MR.strings.cart,
            MR.images.ic_cart,
            NavConstants.CART_SCREEN_ROUTE
        )
}
