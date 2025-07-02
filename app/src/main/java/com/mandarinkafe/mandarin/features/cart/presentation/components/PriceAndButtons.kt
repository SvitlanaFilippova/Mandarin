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
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun PriceAndButtons(
    item: CustomizedMeal,
    itemInPendingDeletion: Boolean,
    quantity: Int,
    deletionProgress: Float,
    contentColor: Color,
    onEditMealClick: () -> Unit,
    onMealDetailsClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onDeletionCancel: () -> Unit,
) {
    val meal = item.meal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Стоимость 1 шт с учётом всех добавок и модификаторов
        Text(
            text = stringResource(R.string.meal_price_template, item.totalPrice()),
            style = Typography.MealPriceStyle,
            color = contentColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if ((meal.isCustomizable || meal.requireSelection) && !itemInPendingDeletion) {
            MealDetailsButton(
                isCustomized = item.isCustomized(),
                onEditMealClick = onEditMealClick,
                onMealDetailsClick = onMealDetailsClick
            )
        }

        CartControlWithUndo(
            numberInCart = quantity,
            item = item,
            mealInPendingDeletion = itemInPendingDeletion,
            deletionProgress = deletionProgress,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onCancel = onDeletionCancel,
        )
    }
}