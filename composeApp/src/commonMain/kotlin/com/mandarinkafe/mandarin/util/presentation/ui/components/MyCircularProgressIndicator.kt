package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun MyCircularProgressIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = Dimens.ProgressBarStroke6,
) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = strokeWidth,
        color = Colors.LightGrey.copy(alpha = 0.8f),
    )
}
