package com.mandarinkafe.mandarin.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun FavoritesScreen() {
    Text(
        modifier = Modifier
            .background(Colors.Transparent)
            .fillMaxSize(), text = "FavoritesScreen",
        style = Typography.MealTitleStyle
    )
}