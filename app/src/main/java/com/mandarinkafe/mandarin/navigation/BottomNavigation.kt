package com.mandarinkafe.mandarin.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.navigation.NavRoutes.MENU_SCREEN_ROUTE

@Composable
fun BottomNavigation(
    navController: NavController,
    cartCount: Int, cartIsPendingDeletion: Boolean
) {
    val context = LocalContext.current
    val listItems = listOf(
        BottomNavigationItem.Menu,
        BottomNavigationItem.Delivery,
        BottomNavigationItem.Favorites,
        BottomNavigationItem.Cart
    )

    BottomAppBar(
        tonalElevation = Dimens.Elevation2,
        containerColor = Colors.AppBlack,
        modifier = Modifier.height(Dimens.BottomBarHeight64)
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = backStackEntry?.destination?.route

        listItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(
                        route = item.route,
                        navOptions = navOptions {
                            launchSingleTop = true
                            popUpTo(MENU_SCREEN_ROUTE) {
                                inclusive = false
                                saveState = true
                            }
                            restoreState = true
                        }
                    )
                },
                icon = {
                    if (item == BottomNavigationItem.Cart && !cartIsPendingDeletion && currentRoute != item.route) {
                        @OptIn(ExperimentalAnimationApi::class)
                        BadgedBox(
                            badge = {
                                AnimatedContent(
                                    targetState = cartCount,
                                    transitionSpec = {
                                        (scaleIn(tween(300)) + fadeIn()).togetherWith(
                                            scaleOut(
                                                tween(
                                                    300
                                                )
                                            ) + fadeOut()
                                        )
                                    }
                                ) { count ->
                                    if (count > 0) {
                                        Badge(
                                            containerColor = Colors.Orange,
                                            contentColor = Colors.AppBlack
                                        ) {
                                            Text(count.toString())
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = context.getString(item.title)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = context.getString(item.title)
                        )
                    }
                },
                label = {
                    Text(
                        text = context.getString(item.title),
                        fontSize = 9.sp
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