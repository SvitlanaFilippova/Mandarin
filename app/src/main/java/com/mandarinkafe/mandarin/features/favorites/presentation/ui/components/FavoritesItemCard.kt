package com.mandarinkafe.mandarin.features.favorites.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.customizedText
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract.CartState
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_TITLE_IN_MENU
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealItemImageBox

/**
 * Компонент для отображения товара, добавленного в избранное. Поддерживает и базовые, и кастомизированные блюда.
 */

@Composable
fun FavoritesItemCard(
    modifier: Modifier,
    item: CustomizedMeal,
    cartState: CartState,
    imageSize: Dp,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
) {

    val meal = item.meal
    val isFavorite =
        true // нет смысла дополнительно проверять, поскольку в этот только избранные попадают

    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = Dimens.MarginSmall8)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
            .clickable(onClick = { onMealDetailsClick(item) })
    ) {

        MealItemImageBox(
            modifier = Modifier
                .size(imageSize)
                .padding(Dimens.MarginSmall8),
            meal = meal,
            isFavorite = isFavorite,
            onToggleFavorite = { onToggleFavorite(item) },
            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = Dimens.MarginSmall8,
                    end = Dimens.MarginSmall8,
                    bottom = Dimens.MarginSmall8
                )
        ) {
            // Название блюда
            Text(
                text = meal.name,
                style = Typography.RegularTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAX_LINES_FOR_MEAL_TITLE_IN_MENU,

                )

            // Описание блюда
            Text(
                text = meal.description,
                style = Typography.MealSmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU,
            )
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            // Выбранные опции кастомизации
            if (item.isCustomized()) {
                Text(
                    text = item.customizedText(),
                    style = Typography.MealSmallTextStyle,
                )

                Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
            }

            // Для выравнивания кнопок
            Spacer(modifier = Modifier.weight(1f))

            // Кнопки
            FavoriteItemButtonRow(
                item = item,
                cartState = cartState,
                onAddToCart = { onAddToCart(item) },
                onRemoveFromCart = { onRemoveFromCart(item) },
                onMealDetailsClick = { onMealDetailsClick(item) },
            )
        }
    }
}
