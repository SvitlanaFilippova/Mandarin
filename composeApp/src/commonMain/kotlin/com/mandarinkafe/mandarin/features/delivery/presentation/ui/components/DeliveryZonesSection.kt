package com.mandarinkafe.mandarin.features.delivery.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DeliveryZonesSection(
    deliveryAreas: List<UiDeliveryArea>,
    locationChosen: Boolean,
    isError: Boolean,
    isLoading: Boolean,
    deliveryArea: UiDeliveryArea?,
    displayAddress: String?,
    initLocation: GeoPoint,
    userLocation: GeoPoint?,
    mapShouldBeVisible: Boolean,
    onCameraMoved: (GeoPoint) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginStandard16),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(MR.images.ic_map),
                    contentDescription = stringResource(MR.strings.delivery_areas),
                    tint = Colors.WhiteTransparent75
                )
                Column(
                    modifier = Modifier
                        .padding(start = Dimens.MarginStandard16, bottom = Dimens.MarginStandard16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
                ) {
                    Text(
                        text = stringResource(MR.strings.delivery_areas),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(MR.strings.delivery_zones_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (mapShouldBeVisible) {
                MapDeliveryScreenContent(
                    deliveryAreas = deliveryAreas,
                    displayAddress = displayAddress,
                    deliveryArea = deliveryArea,
                    isLoading = isLoading,
                    isError = isError,
                    onCameraMoved = onCameraMoved,
                    locationChosen = locationChosen,
                    initLocation = initLocation,
                    userLocation = userLocation
                )
            }

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

