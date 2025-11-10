package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddressSearchResultItem(
    text: String,
    extraText: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = Dimens.MarginSmall8)
                .weight(1f),
        ) {
            Text(
                text = text,
                style = Typography.RegularTextStyle
            )
            extraText?.let {
                Text(
                    text = extraText,
                    style = Typography.SmallLightTextStyle,
                    maxLines = 1
                )
            }
        }
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24),
                painter = painterResource(MR.images.ic_arrow_right),
                contentDescription = stringResource(MR.strings.deliver_to_this_location),
                tint = Colors.LightGrey
            )
        }
    }
}