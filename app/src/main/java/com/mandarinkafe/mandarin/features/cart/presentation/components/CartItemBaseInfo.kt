package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.customizedText
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens.MarginSmall8
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.ALPHA_50
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun CartItemBaseInfo(
    item: CartItem,
    itemInPendingDeletion: Boolean,
    favorites: List<CustomizedMeal>,
    contentColor: Color,
    onToggleFavorite: (CustomizedMeal) -> Unit,
    onShowFavoriteDialog: (CustomizedMeal) -> Unit,
    onCommentAdded: (String) -> Unit
) {
    var showCommentField by remember(item) { mutableStateOf(false) }
    val imageAlpha = remember(itemInPendingDeletion) { if (itemInPendingDeletion) ALPHA_50 else 1f }

    val customizedMeal = item.customizedMeal
    val meal = item.customizedMeal.meal

    val isFavorite by remember(item, favorites) {
        derivedStateOf { item.customizedMeal.isFavorite(favorites) }
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        MealItemImageBox(
            modifier = Modifier
                .size(Dimens.MealSmallImage80)
                .alpha(imageAlpha),
            meal = meal,
            cardIsSmall = true,
            isFavorite = isFavorite,
            onToggleFavorite = {
                if (!isFavorite && customizedMeal.isCustomized) {
                    onShowFavoriteDialog(customizedMeal)
                } else {
                    onToggleFavorite(customizedMeal)
                }
            },

            )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = Dimens.MarginStandard16,
                    bottom = MarginSmall8
                ),
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
            if (customizedMeal.isCustomized) {
                Text(
                    text = customizedMeal.customizedText(),
                    style = Typography.MealSmallTextStyle,
                    color = contentColor,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            if (item.comment.isNotEmpty()) {
                Text(
                    text = item.comment,
                    style = Typography.MealSmallTextStyle,
                    color = contentColor,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        // Кнопка "Добавить комментарий"
        IconButton(
            onClick = { showCommentField = !showCommentField },
            modifier = Modifier
                .size(Dimens.ButtonBox32)
        ) {
            Icon(
                modifier = Modifier.padding(Dimens.MarginSuperSmall4),
                painter = painterResource(R.drawable.ic_add_comment),
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
        }
    }

    if (showCommentField) {
        MyTextField(
            modifier = Modifier.padding(vertical = MarginSmall8),
            value = item.comment,
            labelRes = R.string.comment_for_meal,
            onValueChange = { onCommentAdded(it) }
        )
    }
}

