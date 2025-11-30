package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun OrderTypeChooserVerticalItem(
    modifier: Modifier,
    selected: Boolean,
    label: StringResource,
    icon: ImageResource,
    enabled: Boolean = true,
    isError: Boolean,
) {
    val contentColor = when {
        selected -> Colors.Orange
        !enabled -> Colors.DarkGrey
        else -> Colors.White.copy(alpha = 0.8f)
    }
    val borderColor = when {
        selected -> Colors.Orange.copy(alpha = 0.4f)
        isError -> Colors.Red.copy(alpha = 0.4f)
        else -> Colors.Transparent
    }

    Column(
        modifier = modifier
            .height(Dimens.PaymentChooserHeight)
            .background(color = Colors.DarkGrey, shape = RoundedCornerShape(Dimens.CornerRadius8))
            .border(
                border = BorderStroke(
                    width = Dimens.Border1,
                    color = borderColor
                ),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            )
            .padding(Dimens.MarginSuperSmall4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.padding(Dimens.MarginSuperSmall4).size(Dimens.IconSize24),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(label),
            color = contentColor,
            style = Typography.SmallTextStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OrderTypeChooserHorizontalItem(
    modifier: Modifier,
    selected: Boolean,
    label: StringResource,
    icon: ImageResource,
    enabled: Boolean = true,
    isError: Boolean,
) {
    val contentColor = when {
        selected -> Colors.Orange
        !enabled -> Colors.LightGrey.copy(alpha = 0.3f)
        else -> Colors.White.copy(alpha = 0.8f)
    }
    val borderColor = when {
        selected -> Colors.Orange.copy(alpha = 0.4f)
        isError -> Colors.Red.copy(alpha = 0.4f)
        else -> Colors.Transparent
    }


    Row(
        modifier = modifier
            .height(Dimens.DeliveryChooserHeight)
            .background(color = Colors.DarkGrey, shape = RoundedCornerShape(Dimens.CornerRadius8))
            .border(
                border = BorderStroke(
                    width = Dimens.Border1,
                    color = borderColor
                ),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            )
            .padding(Dimens.MarginSuperSmall4),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.padding(Dimens.MarginSuperSmall4).size(Dimens.IconSize24),
        )
        Text(
            text = stringResource(label),
            color = contentColor,
            style = Typography.SmallTextStyle,
            textAlign = TextAlign.Center
        )
    }
}
