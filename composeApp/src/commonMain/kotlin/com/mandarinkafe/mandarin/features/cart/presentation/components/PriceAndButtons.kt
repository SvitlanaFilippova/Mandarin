package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.UndoIndicator
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PriceAndButtons(
    item: CartItem,
    itemInPendingDeletion: Boolean,
    outOfStock: Boolean,
    isInProgress: Boolean,
    deletionProgress: Float,
    contentColor: Color,
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
            text = stringResource(MR.strings.meal_price_template, item.customizedMeal.totalPrice()),
            style = Typography.MealPriceStyle,
            color = contentColor
        )

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка "сделать вкуснее"/"редактировать"
        val canCustomize = meal.isCustomizable || meal.requireSelection
        val isVisible = !itemInPendingDeletion && !outOfStock
        if (canCustomize && isVisible) {
            MealDetailsButton(
                isCustomized = item.customizedMeal.isCustomized,
                onEditMealClick = onMealDetailsClick,
                onMealDetailsClick = onMealDetailsClick
            )
        }

        if (outOfStock) {
            when {
                !itemInPendingDeletion -> {
                    IconButton(
                        onClick = onRemoveFromCart,
                        modifier = Modifier.size(Dimens.ButtonToCartSmall36)
                    ) {
                        Icon(
                            modifier = Modifier.padding(Dimens.MarginSmall8),
                            painter = painterResource(MR.images.ic_delete),
                            tint = Color.White,
                            contentDescription = stringResource(MR.strings.remove_from_cart),
                        )
                    }
                }

                else -> {
                    UndoIndicator(
                        progress = deletionProgress,
                        onCancel = onDeletionCancel,
                    )
                }
            }

            Spacer(Modifier.size(Dimens.MarginSuperSmall4))
        }

        CartControlWithUndo(
            item = item,
            mealInPendingDeletion = itemInPendingDeletion,
            outOfStock = outOfStock,
            isInProgress = isInProgress,
            deletionProgress = deletionProgress,
            onAddToCart = onAddToCart,
            onRemoveFromCart = onRemoveFromCart,
            onCancel = onDeletionCancel,
        )
    }
}