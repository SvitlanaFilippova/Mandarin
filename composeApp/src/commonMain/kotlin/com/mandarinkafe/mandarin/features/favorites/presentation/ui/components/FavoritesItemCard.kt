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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.customizedText
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_TITLE_IN_MENU
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.MealItemImageBox
import dev.icerock.moko.resources.compose.stringResource

/**
 * Компонент для отображения товара, добавленного в избранное. Поддерживает и базовые, и кастомизированные блюда.
 */

@Composable
fun FavoritesItemCard(
    modifier: Modifier,
    item: CustomizedMeal,
    isInProgress: Boolean,
    cartItems: List<CartItem>,
    imageSize: Dp,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
) {
    val meal = item.meal
    val isFavorite =
        true // нет смысла дополнительно проверять, поскольку сюда только избранные попадают

    val isHidden = item.meal.isHidden
    val baseModifier = modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(horizontal = Dimens.MarginSmall8)
        .clip(RoundedCornerShape(Dimens.CornerRadius8))
        .background(Colors.DarkGrey)

    val finalModifier = if (!isHidden) {
        baseModifier.clickable(onClick = { onMealDetailsClick(item) })
    } else {
        baseModifier.alpha(0.6f)
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = finalModifier
    ) {
        MealItemImageBox(
            modifier = Modifier
                .size(imageSize)
                .padding(Dimens.MarginSmall8),
            meal = meal,
            cardIsSmall = false,
            isFavorite = isFavorite,
            onToggleFavorite = { onToggleFavorite(item) },
            labelSize = LabelSize.MEDIUM,
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
                style = Typography.SmallLightTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU,
            )
            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            // Выбранные опции кастомизации
            if (item.isCustomized) {
                Text(
                    text = item.customizedText(),
                    style = Typography.SmallLightTextStyle,
                )

                Spacer(modifier = Modifier.height(Dimens.MarginStandard16))
            }

            // Для выравнивания кнопок
            Spacer(modifier = Modifier.weight(1f))

            if (!isHidden) {
                // Кнопки
                FavoriteItemButtonRow(
                    item = item,
                    cartItems = cartItems,
                    isInProgress = isInProgress,
                    onAddToCart = { onAddToCart(item) },
                    onRemoveFromCart = { onRemoveFromCart(item) },
                    onMealDetailsClick = { onMealDetailsClick(item) },
                )
            } else {
                // надпись "нет в наличии" в виде неактивной кнопки
                ButtonWithText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shouldBeActive = false,
                    text = stringResource(MR.strings.item_is_temporary_unavailable),
                    onClick = {  },
                )
            }
        }
    }
}
