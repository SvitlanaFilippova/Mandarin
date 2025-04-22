package com.mandarinkafe.mandarin.menu.ui.components.category_tabs

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun SubCategoryTabItem(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                category,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        selectedContentColor = Colors.Orange,
    )
}