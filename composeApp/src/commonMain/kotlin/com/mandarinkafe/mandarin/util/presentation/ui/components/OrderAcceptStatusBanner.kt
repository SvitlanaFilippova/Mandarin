package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants
import dev.icerock.moko.resources.compose.painterResource

/**
 * Баннер статуса приёма заказов: как [TooltipText], но иконка часов и настраиваемый оттенок иконки.
 */
@Composable
fun OrderAcceptStatusBanner(
    modifier: Modifier = Modifier,
    text: String,
    tint: Color,
) {
    Box(
        modifier = modifier
            .border(
                BorderStroke(width = Dimens.Border1, color = tint.copy(Constants.ALPHA_50)),
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
                painter = painterResource(MR.images.ic_clock),
                tint = tint,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
                Text(
                    text = text,
                    style = Typography.SmallTextStyle.copy(color = Colors.WhiteTransparent75),
                )
            }
        }
    }
}
