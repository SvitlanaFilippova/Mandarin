package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun DeliveryTypeChooser(
    chosen: DeliveryType?,
    isError: Boolean,
    onDeliverySelected: (DeliveryType) -> Unit
) {
    val deliveryTypes = remember { DeliveryType.entries.toList() }

    Column {
        if (isError && chosen == null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.MarginSuperSmall4),
                text = stringResource(R.string.choose_delivery_type),
                style = Typography.RegularTextStyle.copy(color = Colors.ErrorRed),
            )
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            deliveryTypes.forEachIndexed { index, item ->
                val selected = item == chosen
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = deliveryTypes.size
                    ),
                    onClick = { onDeliverySelected(item) },
                    selected = selected,
                    colors = SegmentedButtonColors(
                        activeContainerColor = Colors.DarkGrey,
                        activeContentColor = Colors.Orange,
                        activeBorderColor = Colors.AppBlack,
                        inactiveContainerColor = Colors.DarkGrey,
                        inactiveContentColor = Colors.White,
                        inactiveBorderColor = Colors.AppBlack,
                        disabledActiveContainerColor = Colors.AppBlack,
                        disabledActiveContentColor = Colors.White,
                        disabledActiveBorderColor = Colors.White,
                        disabledInactiveContainerColor = Colors.AppBlack,
                        disabledInactiveContentColor = Colors.White,
                        disabledInactiveBorderColor = Colors.White,
                    ),
                    label = {
                        Text(
                            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                            text = stringResource(item.nameRes),
                            style = Typography.RegularTextStyle,
                            color = if (selected) Colors.Orange else Colors.White
                        )
                    }
                )
            }
        }
    }
}