package com.mandarinkafe.mandarin.features.address.savedadresses.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.features.order.presentation.models.getDetails

@Composable
fun SavedAddressCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    address: UiAddress,
    onAddressChosen: () -> Unit,
    onEditAddress: () -> Unit,
) {
    val details = remember { address.getDetails() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onAddressChosen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.padding(horizontal = Dimens.Margin12),
            colors = RadioButtonDefaults.colors(selectedColor = Colors.Orange),
            selected = selected,
            onClick = null
        )
        Column(
            modifier = Modifier
                .weight(1f)

        ) {
            Text(
                text = address.streetAndBuilding,
                overflow = TextOverflow.Ellipsis,
                style = Typography.RegularTextStyle,
                maxLines = 2,
            )

            if (details.isNotEmpty()) {
                Text(
                    text = address.getDetails(),
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.MealSmallTextStyle,
                    maxLines = 1
                )
            }
        }

        IconButton(
            onClick = onEditAddress,
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(Dimens.MarginSuperSmall4),
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = stringResource(R.string.edit),
                tint = Colors.LightGrey
            )
        }
    }
}