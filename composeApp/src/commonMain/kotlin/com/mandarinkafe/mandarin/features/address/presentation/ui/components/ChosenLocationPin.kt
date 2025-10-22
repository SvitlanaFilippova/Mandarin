package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_INDICATOR_SCALE
import com.mandarinkafe.mandarin.util.ConstantsMap.PIN_INDICATOR_Y_OFFSET_FACTOR
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun ChosenLocationPin(
    modifier: Modifier,
    isLoading: Boolean,
    isError: Boolean,
    addressFound: Boolean
) {
    val iconRes = remember(isLoading, isError, addressFound) {
        when {
            isLoading -> null
            addressFound -> MR.images.ic_home_for_pin
            isError -> MR.images.ic_question
            else -> null
        }
    }
    val indicatorSize = remember { Dimens.MapPinSize * PIN_INDICATOR_SCALE }
    val indicatorOffsetY = remember { Dimens.MapPinSize * PIN_INDICATOR_Y_OFFSET_FACTOR }

    Box(
        modifier = modifier
            .size(Dimens.MapPinSize),
        contentAlignment = Alignment.Center,

        ) {
        // Пин
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(MR.images.ic_empty_pin),
            tint = Colors.Orange,
            contentDescription = null,
        )

        // Индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator(
                color = Colors.Orange,
                strokeWidth = Dimens.ProgressBarStroke6,
                modifier = Modifier
                    .size(indicatorSize)
                    .offset(y = -indicatorOffsetY)
            )
        } else {
            // Иконка внутри
            iconRes?.let {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(iconRes),
                    tint = Colors.Orange,
                    contentDescription = null,
                )
            }
        }
    }
}