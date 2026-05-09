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
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TooltipText(
    modifier: Modifier = Modifier,
    text: String,
    extraText: String? = null,
    extraTextRes: StringResource? = null,
    extraComposable: @Composable (() -> Unit)? = null,
    /** Яркое предупреждение: красная иконка, рамка и контрастнее текст (например, ошибка зоны доставки). */
    useErrorAccent: Boolean = false,
) {
    val shape = RoundedCornerShape(Dimens.CornerRadius8)
    val borderColor = if (useErrorAccent) {
        Colors.Red
    } else {
        Colors.DarkGrey
    }
    val iconTint = if (useErrorAccent) {
        Colors.Red
    } else {
        Colors.WhiteTransparent75
    }
    val bodyColor = if (useErrorAccent) {
        Colors.White
    } else {
        Colors.WhiteTransparent75
    }
    val icon = if (useErrorAccent) {
        MR.images.ic_error
    } else {
        MR.images.ic_info
    }

    Box(
        modifier = modifier
            .border(
                BorderStroke(width = Dimens.Border1, color = borderColor),
                shape = shape
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(
                    start = Dimens.MarginStandard16,
                    top = Dimens.MarginStandard16,
                    bottom = Dimens.MarginStandard16
                ),
                painter = painterResource(icon),
                tint = iconTint,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
                Text(
                    text = text,
                    style = Typography.SmallTextStyle.copy(color = bodyColor),
                )
                extraTextRes?.let {
                    Text(
                        modifier = Modifier.padding(top = Dimens.MarginStandard16),
                        text = stringResource(it),
                        style = Typography.SmallTextStyle.copy(
                            color = bodyColor,
                            fontWeight = FontWeight.Light
                        ),
                    )
                }
                extraText?.let {
                    Text(
                        modifier = Modifier.padding(top = Dimens.MarginStandard16),
                        text = extraText,
                        style = Typography.SmallTextStyle.copy(
                            color = bodyColor,
                            fontWeight = FontWeight.Light
                        ),
                    )
                }

                extraComposable?.let {
                    Box(modifier = Modifier.padding(top = Dimens.MarginStandard16)) {
                        it()
                    }
                }
            }
        }
    }
}
