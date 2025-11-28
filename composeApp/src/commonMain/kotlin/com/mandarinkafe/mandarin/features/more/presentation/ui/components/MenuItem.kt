package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun MenuItem(
    title: String,
    iconRes: ImageResource? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.MarginStandard16)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconRes?.let {
                Icon(
                    modifier = Modifier.padding(end = Dimens.MarginStandard16),
                    painter = painterResource(iconRes),
                    tint = Colors.WhiteTransparent75,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = Typography.RegularTextStyle,
            )

            Icon(
                painter = painterResource(MR.images.ic_arrow_right),
                contentDescription = null,
                tint = Colors.WhiteTransparent75
            )
        }
        HorizontalDivider(
            Modifier.height(Dimens.DividerHeight1),
        )
    }
}