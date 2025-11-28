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
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.navigation.bottomnav.components.CartIconBox
import com.mandarinkafe.mandarin.navigation.bottomnav.components.rememberAdaptiveBottomNavFontSize
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BottomNavigation(
    visible: Boolean,
    navController: NavController,
    cartCount: Int,
    currentRoute: String?,
) {
    val arrayOFItems = BottomNavigationItem.entries.toTypedArray()

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
                        navController.navigate(
                            route = item.route,
                            navOptions {
                                launchSingleTop = true
                            }
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
                            fontSize = rememberAdaptiveBottomNavFontSize(),
                            maxLines = 1
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
