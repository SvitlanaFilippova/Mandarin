package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CustomizeButtonWithText

@Composable
fun MealDetailsButton(
    isCustomized: Boolean,
    onEditMealClick: () -> Unit,
    onMealDetailsClick: () -> Unit,
) {
    when {
        isCustomized -> {
            // Кнопка "Редактировать"
            CustomizeButtonWithText(
                onClick = onEditMealClick,
                text = stringResource(id = R.string.edit_meal),
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
            )
        }

        else -> {
            // Кнопка "Сделать вкуснее"
            CustomizeButtonWithText(
                onClick = onMealDetailsClick,
                text = stringResource(R.string.add_additionals),
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
            )
        }
    }
}