package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.models.UiAddressType
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.models.toDomain
import com.mandarinkafe.mandarin.util.presentation.ui.components.SegmentedButtonLabel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddressTypeChooser(
    chosen: AddressType?,
    isError: Boolean,
    onItemSelected: (AddressType) -> Unit,
) {
    val types = remember { UiAddressType.entries.toList() }

    val style = if (isError && chosen == null) {
        Typography.RegularTextStyle.copy(color = Colors.Red)
    } else {
        Typography.RegularTextStyle
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4),
        text = stringResource(MR.strings.address_type),
        style = style,
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        types.forEachIndexed { index, item ->
            val selected = item.name == chosen?.name
            val borderColor = when {
                selected -> Colors.Orange
                isError && chosen == null -> Colors.Red
                else -> Colors.AppBlack
            }

            SegmentedButton(
                modifier = Modifier.height(Dimens.BigButtonWithTextHeight),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = types.size,
                    baseShape = RoundedCornerShape(Dimens.CornerRadius8)
                ),
                onClick = { onItemSelected(item.toDomain()) },
                selected = selected,
                colors = SegmentedButtonColors(
                    activeContainerColor = Colors.DarkGrey,
                    activeContentColor = Colors.Orange,
                    activeBorderColor = borderColor,
                    inactiveContainerColor = Colors.DarkGrey,
                    inactiveContentColor = Colors.White,
                    inactiveBorderColor = borderColor,
                    disabledActiveContainerColor = Colors.AppBlack,
                    disabledActiveContentColor = Colors.DarkGrey,
                    disabledActiveBorderColor = Colors.AppBlack,
                    disabledInactiveContainerColor = Colors.DarkGrey,
                    disabledInactiveContentColor = Colors.AppBlack,
                    disabledInactiveBorderColor = Colors.AppBlack,
                ),
                icon = { },
                label = {
                    SegmentedButtonLabel(
                        selected = selected,
                        name = stringResource(item.nameRes),
                        icon = painterResource(item.iconRes),
                    )
                }
            )
        }
    }
}
