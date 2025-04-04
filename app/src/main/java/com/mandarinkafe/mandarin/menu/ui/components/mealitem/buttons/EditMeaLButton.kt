package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun EditMealButton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { /* Действие */ },
            modifier = Modifier.size(Dimens.ButtonToCartSmall32)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_additionals),
                contentDescription = "Редактировать",
                modifier = Modifier.size(Dimens.ButtonEditMeal32),
                tint = Color.Unspecified
            )
        }
    }
}