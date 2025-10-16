package com.mandarinkafe.mandarin.navigation.bottomnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.navigation.bottomnav.components.CartIconBox
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun BottomNavigation(
    visible: Boolean,
    navigator: Navigator,
    cartCount: Int,
    currentRoute: String?,
) {
    val arrayOFItems = arrayOf(
        BottomNavigationItem.Menu,
        BottomNavigationItem.Favorites,
        BottomNavigationItem.Cart,
        BottomNavigationItem.Other,
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        NavigationBar(
            containerColor = Colors.AppBlack,
        ) {
            val routeWithoutArgs = currentRoute?.substringBefore("?")

            arrayOFItems.forEach { item ->
                NavigationBarItem(
                    selected = routeWithoutArgs == item.route,
                    onClick = {
                        navigator.navigate(
                            route = item.route,
                            options = moe.tlaster.precompose.navigation.NavOptions(
                                launchSingleTop = true
                            )
                        )
                    },
                    icon = {
                        val painter: Painter = painterResource(item.icon)
                        
                        if (item == BottomNavigationItem.Cart && routeWithoutArgs != item.route) {
                            CartIconBox(
                                cartCount = cartCount,
                                painterResource = painter,
                                stringResource = stringResource(item.title)
                            )
                        } else {
                            Icon(
                                painter = painter,
                                contentDescription = stringResource(item.title)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(item.title),
                            fontSize = Dimens.TextSizeSuperSmall10
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Colors.Orange,
                        selectedTextColor = Colors.Orange,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}
