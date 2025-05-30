package com.mandarinkafe.mandarin.shared.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.customizedText
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.isFavorite
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.ui.components.buttons.CustomizeButtonWithText

/**
 * Компонент, который отвечает за отображение товара, который выбрали в меню
 */

@Composable
fun CartItemCard(
    item: CustomizedMeal,
    quantity: Int,
    itemInPendingDeletion: Boolean,
    favorites: List<CustomizedMeal>,
    deletionProgress: Float,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
    onDeletionCancel: (CustomizedMeal) -> Unit,
    onEditMealClick: (CustomizedMeal) -> Unit,
) {
    val meal = item.meal
    val contentColor = if (itemInPendingDeletion) Colors.GreyTransparent75 else Colors.White
    val imageAlpha = if (itemInPendingDeletion) 0.5f else 1f

    val isFavorite by remember(favorites) {
        derivedStateOf { item.isFavorite(favorites) }
    }
    val onItemClick = if (item.isCustomized()) {
        { onMealDetailsClick(item) }
    } else {
        { onEditMealClick(item) }
    }

    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable { onItemClick }

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
                isFavorite = isFavorite,
                onToggleFavorite = { onToggleFavorite(item) },
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
                if (item.isCustomized()) {
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

            if ((meal.isCustomizable() || meal.requireSelection) && !itemInPendingDeletion) {
                when {
                    item.isCustomized() -> {
                        // Кнопка "Редактировать"
                        CustomizeButtonWithText(
                            onClick = { onEditMealClick(item) },
                            text = stringResource(id = R.string.edit_meal),
                            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
                        )
                    }

                    else -> {
                        // Кнопка "Сделать вкуснее"
                        CustomizeButtonWithText(
                            onClick = { onMealDetailsClick(item) },
                            text = stringResource(R.string.add_additionals),
                            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
                        )
                    }
                }
            }

            CartControlWithUndo(
                numberInCart = quantity,
                item = item,
                mealInPendingDeletion = itemInPendingDeletion,
                deletionProgress = deletionProgress,
                onAddToCart = { onAddToCart(item) },
                onRemoveFromCart = { onRemoveFromCart(item) },
                onCancel = { onDeletionCancel(item) },
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
