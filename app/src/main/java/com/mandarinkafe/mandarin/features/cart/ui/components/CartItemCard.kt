package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.customizedText
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.totalPrice
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox

/**
 * Компонент, который отвечает за отображение товара, который выбрали в меню
 */

@Composable
fun CartItemCard(
    item: CartItem,
    quantity: Int,
    itemInPendingDeletion: Boolean,
    deletionProgress: Float,
    onEvent: (CartContract.CartEvent) -> Unit
) {
    val meal = item.meal
    val contentColor = if (itemInPendingDeletion) Colors.GreyTransparent75 else Colors.White
    val imageAlpha = if (itemInPendingDeletion) 0.5f else 1f
    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable(onClick = { onEvent(CartContract.CartEvent.OpenMealDetails(item)) })
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            MealItemImageBox(
                modifier = Modifier
                    .size(Dimens.MealSmallImage80)
                    .alpha(imageAlpha),
                meal = meal,
                onToggleFavorite = { }, //TODO
            )

            Column(
                modifier = Modifier
                    .padding(
                        start = Dimens.MarginStandard16,
                        bottom = Dimens.MarginSmall8
                    )
                    .fillMaxWidth()
            ) {
                // Название блюда
                Text(
                    text = meal.name,
                    style = Typography.RegularTextStyle,
                    color = contentColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Выбранные опции кастомизации
                if (item.adds.isNotEmpty() || item.modifiers.isNotEmpty()) {
                    Text(
                        text = item.customizedText(),
                        style = Typography.MealSmallTextStyle,
                        color = contentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }

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

            if (meal.editableType != null && !itemInPendingDeletion) {
                // Кнопка "Редактировать"
                Box(modifier = Modifier.padding(horizontal = Dimens.MarginStandard16)) {
                    IconButton(
                        onClick = { onEvent(CartContract.CartEvent.OpenMealDetails(item)) },
                        modifier = Modifier
                            .size(Dimens.ButtonToCartSmall32)
                    ) {
                        Icon(
                            modifier = Modifier.padding(Dimens.MarginSmall8),
                            imageVector = Icons.Default.Edit,
                            tint = Color.White,
                            contentDescription = stringResource(id = R.string.edit_meal),
                        )
                    }
                }
            }

            CartControlWithUndo(
                numberInCart = quantity,
                item = item,
                mealInPendingDeletion = itemInPendingDeletion,
                onEvent = onEvent,
                deletionProgress = deletionProgress,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            thickness = Dimens.DividerHeight1,
            color = Colors.LightGrey.copy(alpha = 0.2f)
        )
    }
}


