package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun SegmentedButtonLabel(
    @StringRes nameRes: Int,
    @DrawableRes iconRes: Int,
    selected: Boolean,
    isEnabled: Boolean = true
) {
    val color = if (selected) {
        Colors.Orange
    } else if (!isEnabled) {
        Colors.LightGrey.copy(
            alpha = 0.2f
        )
    } else {
        Colors.White
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(Dimens.IconSize20),
            painter = painterResource(iconRes),
            contentDescription = stringResource(nameRes),
            tint = color
        )

        Text(
            modifier = Modifier.padding(horizontal = Dimens.MarginSuperSmall2),
            text = stringResource(nameRes),
            style = Typography.SmallTextStyle,
            color = color
        )
    }
}