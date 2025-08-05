package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun PriceAndButtons(
    item: CartItem,
    itemInPendingDeletion: Boolean,
    deletionProgress: Float,
    contentColor: Color,
    onEditMealClick: () -> Unit,
    onMealDetailsClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onDeletionCancel: () -> Unit,
) {
    val meal = item.customizedMeal.meal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Стоимость 1 шт с учётом всех добавок и модификаторов
        Text(
            text = stringResource(R.string.meal_price_template, item.customizedMeal.totalPrice()),
            style = Typography.MealPriceStyle,
            color = contentColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if ((meal.isCustomizable || meal.requireSelection) && !itemInPendingDeletion) {
            MealDetailsButton(
                isCustomized = item.customizedMeal.isCustomized,
                onEditMealClick = onEditMealClick,
                onMealDetailsClick = onMealDetailsClick
            )
        }

        CartControlWithUndo(
            item = item,
            mealInPendingDeletion = itemInPendingDeletion,
            deletionProgress = deletionProgress,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onCancel = onDeletionCancel,
        )
    }
}