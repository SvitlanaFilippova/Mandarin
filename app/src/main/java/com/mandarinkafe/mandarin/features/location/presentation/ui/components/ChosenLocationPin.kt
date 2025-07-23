package com.mandarinkafe.mandarin.features.location.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun ChosenLocationPin(
    modifier: Modifier
) {
    Icon(
        painter = painterResource(R.drawable.ic_pin_dot),
        tint = Colors.Orange,
        contentDescription = null,
        modifier = modifier
            .size(Dimens.MapPinSize)
    )
}