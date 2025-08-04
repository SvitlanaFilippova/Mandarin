package com.mandarinkafe.mandarin.navigation.bottomnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.navigation.NavConstants.MAIN_GRAPH
import com.mandarinkafe.mandarin.navigation.bottomnav.components.CartIconBox

@Composable
fun BottomNavigation(
    visible: Boolean,
    navController: NavController,
    cartCount: Int,
) {
    val arrayOFItems = arrayOf(
        BottomNavigationItem.Search,
        BottomNavigationItem.Favorites,
        BottomNavigationItem.Menu,
        BottomNavigationItem.Cart,
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        BottomAppBar(
            tonalElevation = Dimens.Elevation2,
            containerColor = Colors.AppBlack,
        ) {
            val backStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = backStackEntry?.destination?.route?.substringBefore("?")

            arrayOFItems.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(
                            route = item.route,
                            navOptions = navOptions {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(MAIN_GRAPH) {
                                    saveState = true
                                }
                            }
                        )
                    },
                    icon = {
                        if (item == BottomNavigationItem.Cart && currentRoute != item.route) {
                            CartIconBox(
                                cartCount = cartCount,
                                painterResource = item.icon,
                                stringResource = item.title
                            )
                        } else {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = stringResource(item.title)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(item.title),
                            fontSize = Dimens.TextSizeSuperSmall9
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Colors.Orange,
                        selectedTextColor = Colors.Orange,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent
                    ),
                    alwaysShowLabel = false
                )
            }
        }
    }
}