package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CustomizeButtonWithText

@Composable
fun MealDetailsButton(
    item: CustomizedMeal,
    onEditMealClick: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
) {
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