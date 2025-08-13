package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun TooltipText(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int,
    @StringRes extraTextRes: Int? = null
) {
    Box(
        modifier = modifier
            .border(
                BorderStroke(width = Dimens.Border1, color = Colors.DarkGrey),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(
                    start = Dimens.MarginStandard16,
                    top = Dimens.MarginStandard16,
                    bottom = Dimens.MarginStandard16
                ),
                imageVector = Icons.Default.Info,
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
                Text(
                    text = stringResource(textRes),
                    style = Typography.SmallTextStyle.copy(
                        color = Colors.WhiteTransparent75,
                        fontWeight = FontWeight.Medium
                    ),
                )
                extraTextRes?.let {
                    Text(
                        modifier = Modifier.padding(top = Dimens.MarginStandard16),
                        text = stringResource(extraTextRes),
                        style = Typography.SmallTextStyle.copy(color = Colors.WhiteTransparent75),
                    )
                }
            }
        }
    }
}