package com.mandarinkafe.mandarin.navigation.bottomnav

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.navigation.NavConstants
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class BottomNavigationItem(
    val title: StringResource, 
    val icon: ImageResource, 
    val route: String
) {
    Menu(
        MR.strings.menu,
        MR.images.ic_menu_book,
        NavConstants.MENU_SCREEN_ROUTE
    ),
    
    Search(
        MR.strings.search,
        MR.images.ic_search_big,
        NavConstants.SEARCH_SCREEN_ROUTE
    ),
    
    Favorites(
        MR.strings.favorite,
        MR.images.ic_favorite_inactive,
        NavConstants.FAVORITES_SCREEN_ROUTE
    ),
    
    Cart(
        MR.strings.cart,
        MR.images.ic_cart,
        NavConstants.CART_SCREEN_ROUTE
    ),
    
    Other(
        MR.strings.more_options,
        MR.images.ic_more,
        NavConstants.MORE_MENU_SCREEN_ROUTE
    )
}
