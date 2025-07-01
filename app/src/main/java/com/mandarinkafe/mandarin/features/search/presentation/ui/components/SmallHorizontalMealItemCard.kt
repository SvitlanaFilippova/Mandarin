package com.mandarinkafe.mandarin.features.search.presentation.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartContract
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MealButtonsRow

@Composable
fun SmallHorizontalMealItemCard(
    modifier: Modifier = Modifier,
    meal: Meal,
    favoriteIds: Set<String>,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    cartState: CartContract.CartState,
) {
    val isFavorite by remember(favoriteIds) {
        derivedStateOf { meal.isFavorite(favoriteIds) }
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .padding(vertical = Dimens.DividerHeight1)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
            .clickable { onMealDetailsClick(meal) }
    ) {
        MealItemImageBox(
            modifier = Modifier
                .size(Dimens.MealItemInSearchResults96)
                .padding(Dimens.MarginSmall8),
            meal = meal,
            isFavorite = isFavorite,
            onToggleFavorite = { onToggleFavorite(meal) },
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
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
            )
            val parentCategoryName =
                with(meal) { if (!grandParentCategoryName.isNullOrEmpty()) "$grandParentCategoryName / $parentCategoryName" else parentCategoryName }

            // Родительская категория
            Text(
                text = parentCategoryName,
                style = Typography.MealSmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.weight(1f))

                MealButtonsRow(
                    modifier = Modifier
                        .width(Dimens.ButtonsRowWidth164)
                        .padding(top = Dimens.MarginSmall8),
                    baseMeal = meal,
                    cartState = cartState,
                    onAddToCart = { onAddToCart(meal) },
                    onRemoveFromCart = { onRemoveFromCart(meal) },
                    onMealDetailsClick = { onMealDetailsClick(meal) },

                    )
            }
        }
    }
}

