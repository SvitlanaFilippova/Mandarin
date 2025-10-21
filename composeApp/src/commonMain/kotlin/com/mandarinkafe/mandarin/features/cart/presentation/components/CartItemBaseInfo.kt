package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
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
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.MealItemImageBox
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealCommentTextField
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

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
    var showCommentField by remember { mutableStateOf(false) }
    val imageAlpha = remember(itemInPendingDeletion) { if (itemInPendingDeletion) ALPHA_50 else 1f }
    val iconRes =
        remember(item) { if (item.comment.isEmpty()) MR.images.ic_comment_add else MR.images.ic_comment_edit }
    val customizedMeal = item.customizedMeal
    val meal = item.customizedMeal.meal

    val isFavorite by remember(customizedMeal, favorites) {
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
            labelSize = LabelSize.SMALL,
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
                modifier = Modifier.fillMaxWidth(),
                text = meal.name,
                style = Typography.RegularTextStyle,
                color = contentColor,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,

                )
            // Выбранные опции кастомизации
            if (customizedMeal.isCustomized) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = customizedMeal.customizedText(),
                    style = Typography.MealSmallTextStyle,
                    color = contentColor,

                    )
            }
            // Комментарий
            AnimatedVisibility(
                visible = item.comment.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MarginSmall8),
                    text = "Комментарий: ${item.comment}",
                    style = Typography.MealSmallTextStyle,
                    color = contentColor,
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
                painter = painterResource(iconRes),
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
        }
    }

    if (showCommentField) {
        MealCommentTextField(
            modifier = Modifier.padding(vertical = MarginSmall8),
            initialValue = item.comment,
            labelRes = stringResource(MR.strings.comment_for_meal),
            onCommentSubmitted = {
                onCommentAdded(it)
                showCommentField = false
            }
        )
    }
}