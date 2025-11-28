package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiDeliveryType
import com.mandarinkafe.mandarin.features.order.presentation.models.toDomain

@Composable
fun DeliveryTypeChooser(
    chosen: DeliveryType?,
    isError: Boolean,
    onDeliverySelected: (DeliveryType) -> Unit,
    deliveryEnabled: Boolean,
) {
    val deliveryTypes = remember { UiDeliveryType.entries.toList() }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSmall8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        deliveryTypes.forEach { item ->
            val selected = item.toDomain() == chosen

            val isDelivery = item == UiDeliveryType.DELIVERY
            val itemEnabled = if (isDelivery) deliveryEnabled else true
            val modifier = if (itemEnabled) {
                Modifier.weight(1f)
                    .clickable(
                        onClick = { onDeliverySelected(item.toDomain()) },
                        role = Role.Button
                    )
            } else {
                Modifier
                    .weight(1f)
            }

            OrderTypeChooserHorizontalItem(
                modifier = modifier,
                selected = selected,
                label = item.nameRes,
                icon = item.iconRes,
                enabled = itemEnabled,
                isError = isError && chosen == null
            )
        }

    }
}