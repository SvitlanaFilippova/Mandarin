package com.mandarinkafe.mandarin.features.address.address.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun AddressSearchResultItem(
    text: String,
    extraText: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
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
                    style = Typography.RegularLightTextStyle,
                    maxLines = 2
                )
            }
        }
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.deliver_to_this_location),
                tint = Colors.LightGrey
            )
        }
    }
}