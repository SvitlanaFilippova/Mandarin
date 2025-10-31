package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.RoundedButton
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MapButtons(
    modifier: Modifier,
    onBackToInitLocationClick: (() -> Unit)?,
    onBackToUserLocationClick: (() -> Unit)? = null,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(end = Dimens.MarginSmall8)
    ) {
        // Кнопка "Вернуться к стартовой позиции"
        onBackToInitLocationClick?.let {
            RoundedButton(
                onClick = onBackToInitLocationClick,
                painter = painterResource(MR.images.ic_undo),
                contentDescription = stringResource(MR.strings.to_init_location)
            )
        }

        // Кнопка "Вернуться к позиции пользователя"
        onBackToUserLocationClick?.let {
            RoundedButton(
                modifier = Modifier.padding(top = Dimens.MarginSmall8),
                onClick = onBackToUserLocationClick,
                painter = painterResource(MR.images.ic_my_location),
                contentDescription = stringResource(MR.strings.to_my_location)
            )
        }

        // Кнопка "Приблизить"
        RoundedButton(
            modifier = Modifier.padding(top = Dimens.MarginSmall8),
            onClick = onZoomIn,
            painter = painterResource(MR.images.ic_plus),
            contentDescription = stringResource(MR.strings.zoom_plus)
        )

        // Кнопка "Отдалить"
        RoundedButton(
            modifier = Modifier.padding(top = Dimens.MarginSmall8),
            onClick = onZoomOut,
            painter = painterResource(MR.images.ic_minus),
            contentDescription = stringResource(MR.strings.zoom_minus)
        )

    }
}