package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AlertAboutPickupOnly(
    pickupOnly: Boolean,
    containsAlcohol: Boolean,
    pickupOnlyPositionsNames: List<String>,
    onRemovePickupOnly: () -> Unit
) {
    val extraComposable: (@Composable () -> Unit)? =
        if (pickupOnlyPositionsNames.isNotEmpty()) {
            {
                PickupOnlyPositionsTooltip(
                    pickupOnlyPositionsNames = pickupOnlyPositionsNames,
                    onRemovePickupOnly = onRemovePickupOnly
                )
            }
        } else {
            null
        }

    when {
        containsAlcohol -> {
            TooltipText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.MarginStandard16),
                text = stringResource( MR.strings.alcohol_pickup_only_18_plus),
                extraComposable = extraComposable
            )
        }

        pickupOnly -> {
            TooltipText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.MarginStandard16),
                text = stringResource( MR.strings.pickup_only),
                extraComposable = extraComposable
            )
        }
    }
}
