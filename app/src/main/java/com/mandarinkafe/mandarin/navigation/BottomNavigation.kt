package com.mandarinkafe.mandarin.navigation

import androidx.compose.foundation.layout.height
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
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun BottomNavigation(
    navController: NavController
) {
    val context = LocalContext.current
    val listItems = listOf(
        BottomNavigationItem.Delivery,
        BottomNavigationItem.Menu,
        BottomNavigationItem.Favorites
    )
    BottomAppBar(
        containerColor = Colors.AppBlack,
        modifier = Modifier.height(Dimens.BottomBarHeight64)
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = backStackEntry?.destination?.route
        listItems.forEach {
            NavigationBarItem(
                selected = currentRoute == it.route,
                onClick = {
                    navController.navigate(it.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(it.icon),
                        contentDescription = context.getString(it.title)
                    )
                },
                label = {
                    Text(
                        text = context.getString(it.title),
                        fontSize = 9.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(context.getColor(R.color.orange)),
                    selectedTextColor = Color(context.getColor(R.color.orange)),
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                ),
                alwaysShowLabel = false
            )
        }
    }
}