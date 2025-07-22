package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.mandarinkafe.mandarin.util.presentation.ui.components.RadiobuttonWithTextRow

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
        deliveryTypes.forEach { item ->
            RadiobuttonWithTextRow(
                label = stringResource(item.nameRes),
                selected = item == chosen,
                onItemSelected = { onDeliverySelected(item) }
            )
        }
    }
}