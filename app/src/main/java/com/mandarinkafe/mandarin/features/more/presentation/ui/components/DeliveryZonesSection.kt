package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea

@Composable
fun DeliveryZonesSection(deliveryAreas: List<UiDeliveryArea>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = Dimens.MarginStandard16,
                horizontal = Dimens.MarginSmall8
            )
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                text = stringResource(R.string.delivery_areas),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.delivery_zones_text),
                style = MaterialTheme.typography.bodyMedium
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
            ) {
                deliveryAreas.forEach { area ->
                    DeliveryAreaCompactInfo(
                        modifier = Modifier
                            .weight(1f),
                        deliveryArea = area
                    )
                }
            }
        }
    }
}
