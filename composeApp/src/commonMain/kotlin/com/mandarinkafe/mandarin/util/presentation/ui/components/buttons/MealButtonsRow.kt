package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MealButtonsRow(
    modifier: Modifier = Modifier,
    baseMeal: Meal,
    isInProgress: Boolean,
    cartItems: List<CartItem>,
    onMealDetailsClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
) {
    // Мемоизируем вычисления для конкретного блюда
    // Извлекаем только нужные данные из cartItems для этого meal.id
    val mealCartData = remember(cartItems, baseMeal.id) {
        val mealItems = cartItems.filter { it.customizedMeal.meal.id == baseMeal.id }
        val isInTheCart = mealItems.isNotEmpty()
        val totalPrice = if (isInTheCart) {
            mealItems.sumOf { it.customizedMeal.totalPrice() * it.quantity }
        } else {
            0
        }
        val numberInCart = if (isInTheCart) {
            mealItems.sumOf { it.quantity }
        } else {
            0
        }
        Triple(isInTheCart, totalPrice.toDouble(), numberInCart.toDouble())
    }

    val isInTheCart = mealCartData.first
    val totalPrice = mealCartData.second
    val numberInCart = mealCartData.third

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall36)
            .weight(1f)

        if (baseMeal.isCustomizable) {
            CustomizeButton(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                onClick = onMealDetailsClick
            )
        }

        when {
            isInProgress -> ButtonWithCircularProgressIndicator(modifier = modifier)

            isInTheCart -> {
                CartControls(
                    modifier = modifier,
                    numberInCart = numberInCart,
                    totalPrice = totalPrice,
                    onIncrease = onAddToCart,
                    onDecrease = onRemoveFromCart,
                )
            }

            baseMeal.requireSelection -> {
                SelectButton(
                    text = stringResource(MR.strings.to_choose),
                    onClick = onMealDetailsClick,
                    modifier = modifier
                )
            }

            else -> {
                ToCartButtonWithPrice(
                    price = baseMeal.price.toDouble(),
                    onClick = onAddToCart,
                    modifier = modifier,
                )
            }
        }
    }
}
