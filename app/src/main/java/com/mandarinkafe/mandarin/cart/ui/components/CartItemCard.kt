package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.CartControlWithUndo

/**
 * Компонент, который отвечает за отображение товара, который выбрали в меню
 */

@Composable
fun CartItemCard(
    item: CartItem,
    mealInPendingDeletion: Boolean,
    deletionProgress: Float,
    onEvent: (CartContract.Event) -> Unit
) {
    val meal = item.meal
    val totalPrice = meal.price + meal.adds.sumOf { it.price }

    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(horizontal = Dimens.MarginStandard16)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // Изображение блюда
            AsyncImage(
                model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
                contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimens.MealSmallImage64)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
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
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Выбранные добавки
                if (meal.adds.isNotEmpty()) {
                    val addsText = meal.adds.joinToString(", ") { it.name }
                    Text(
                        text = stringResource(R.string.adds_prefix, addsText),
                        style = Typography.MealSmallTextStyle,
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
                text = stringResource(R.string.meal_price_template, totalPrice),
                style = Typography.MealPriceStyle,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))


            if (meal.editableType != null && !mealInPendingDeletion) {
                // Кнопка "Редактировать"
                Box(modifier = Modifier.padding(horizontal = Dimens.MarginStandard16)) {
                    IconButton(
                        onClick = { onEvent(CartContract.Event.EditMeal(meal)) },
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
                numberInCart = item.quantity,
                totalPrice = totalPrice,
                meal = meal,
                mealInPendingDeletion = mealInPendingDeletion,
                onEvent = onEvent,
                deletionProgress = deletionProgress,
            )

        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            thickness = Dimens.DividerHeight1,
            color = Colors.Grey.copy(alpha = 0.2f)
        )
    }
}

