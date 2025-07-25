package com.mandarinkafe.mandarin.features.address.map.presentation.ui.components

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
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun ChosenLocationPin(
    modifier: Modifier,
    isLoading: Boolean,
    isError: Boolean,
    addressFound: Boolean
) {
    val iconRes = when {
        isError -> R.drawable.ic_question
        isLoading -> null
        addressFound -> R.drawable.ic_home
        else -> null
    }
    val indicatorSize = remember { Dimens.MapPinSize * 0.45f }
    val indicatorOffsetY = remember { Dimens.MapPinSize * 0.09f }
    Box(
        modifier = modifier
            .size(Dimens.MapPinSize),
        contentAlignment = Alignment.Center,

        ) {
        // Пин
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.ic_empty_pin),
            tint = Colors.Orange,
            contentDescription = null,
        )
        // Иконка внутри
        iconRes?.let {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(iconRes),
                tint = Colors.Orange,
                contentDescription = null,
            )
        }
        // Индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator(
                color = Colors.Orange,
                strokeWidth = Dimens.ProgressBarStroke6,
                modifier = Modifier
                    .size(indicatorSize)
                    .offset(y = -indicatorOffsetY)
            )
        }
    }
}