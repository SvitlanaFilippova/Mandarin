package com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.getDetailsString
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SavedAddressCard(
    modifier: Modifier = Modifier,
    selected: Boolean? = null,
    address: Address,
    onAddressChosen: () -> Unit,
    onEditAddress: () -> Unit,
    onRemoveAddress: () -> Unit,
) {
    val details = address.getDetailsString()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4)
            .clickable(onClick = onAddressChosen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        selected?.let {
            RadioButton(
                modifier = Modifier.padding(horizontal = Dimens.Margin12),
                colors = RadioButtonDefaults.colors(selectedColor = Colors.Orange),
                selected = selected,
                onClick = null
            )
        }
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
                    text = details,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.SmallLightTextStyle,
                    maxLines = 2
                )
            }
        }
        IconButton(
            onClick = onEditAddress,
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(Dimens.MarginSuperSmall2),
                painter = painterResource(MR.images.ic_edit),
                contentDescription = stringResource(MR.strings.edit),
                tint = Colors.LightGrey
            )
        }

        IconButton(
            onClick = onRemoveAddress,
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(Dimens.MarginSuperSmall2),
                painter = painterResource(MR.images.ic_delete),
                contentDescription = stringResource(MR.strings.remove),
                tint = Colors.Red
            )
        }
    }
}