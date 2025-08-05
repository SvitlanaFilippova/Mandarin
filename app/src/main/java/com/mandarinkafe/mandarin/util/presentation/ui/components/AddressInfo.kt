package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.getDetailsString
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.Label
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.Value

@Composable
fun AddressInfo(
    address: Address?
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginSmall8)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                imageVector = Icons.Default.LocationOn,
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                address?.let {
                    Spacer(Modifier.height(Dimens.MarginSmall8))
                    Label("Адрес")
                    Value(it.streetAndBuilding)
                    val details = remember { it.getDetailsString() }
                    if (details.isNotEmpty()) {
                        Value(details)
                    }
                }
                Spacer(Modifier.height(Dimens.MarginSmall8))

            }
        }
    }
}