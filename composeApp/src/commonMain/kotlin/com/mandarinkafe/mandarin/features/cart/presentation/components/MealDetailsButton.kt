package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CustomizeButtonWithText
import dev.icerock.moko.resources.compose.stringResource

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
                text = stringResource(MR.strings.edit),
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
            )
        }

        else -> {
            // Кнопка "Сделать вкуснее"
            CustomizeButtonWithText(
                onClick = onMealDetailsClick,
                text = stringResource(MR.strings.add_additionals),
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8)
            )
        }
    }
}
