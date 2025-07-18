package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.util.presentation.ui.components.RadiobuttonWithTextRow

@Composable
fun DeliveryTypeChooser(
    chosen: DeliveryType,
    onDeliverySelected: (DeliveryType) -> Unit
) {
    val deliveryTypes = remember { DeliveryType.entries.toList() }

    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.shipping_type),
            style = Typography.RegularTextStyle,
        )
        deliveryTypes.forEach { item ->
            RadiobuttonWithTextRow(
                label = stringResource(item.nameRes),
                selected = item == chosen,
                onItemSelected = { onDeliverySelected(item) }
            )
        }
    }
}