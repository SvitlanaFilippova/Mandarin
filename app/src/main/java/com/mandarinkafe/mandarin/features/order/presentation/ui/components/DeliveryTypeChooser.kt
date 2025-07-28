package com.mandarinkafe.mandarin.features.order.presentation.ui.components

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
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun DeliveryTypeChooser(
    chosen: DeliveryType?,
    isError: Boolean,
    onDeliverySelected: (DeliveryType) -> Unit,
    pickupOnly: Boolean
) {
    val deliveryTypes = remember { DeliveryType.entries.toList() }
    val borderColor = if (isError && chosen == null) Colors.ErrorRed else Colors.AppBlack

    val style = if (isError && chosen == null) {
        Typography.RegularTextStyle.copy(color = Colors.ErrorRed)
    } else {
        Typography.RegularTextStyle
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4),
        text = stringResource(R.string.delivery_type),
        style = style,
    )

    if (pickupOnly) {
        TooltipText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.MarginStandard16),
            textRes = R.string.pickup_only
        )
    }

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        deliveryTypes.forEachIndexed { index, item ->
            val isDelivery = item == DeliveryType.DELIVERY
            val itemEnabled = !(pickupOnly && isDelivery)
            val selected = item == chosen
            SegmentedButton(
                modifier = Modifier.height(Dimens.BigButtonWithTextHeight),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = deliveryTypes.size,
                    baseShape = RoundedCornerShape(Dimens.CornerRadius8)
                ),
                onClick = { onDeliverySelected(item) },
                selected = selected,
                enabled = itemEnabled,
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
                label = {
                    Text(
                        modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                        text = stringResource(item.nameRes),
                        style = Typography.RegularTextStyle,
                        color = if (selected) {
                            Colors.Orange
                        } else if (!itemEnabled) {
                            Colors.LightGrey.copy(
                                alpha = 0.2f
                            )
                        } else {
                            Colors.White
                        }
                    )
                }
            )
        }
    }
}