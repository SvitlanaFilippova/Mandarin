package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun PickupOnlyPositionsTooltip(
    pickupOnlyPositionsNames: List<String>,
    onRemovePickupOnly: () -> Unit
) {
    val onlyPickUpPositionsText = stringResource(
        R.string.positions_for_pickup_only_template,
        pickupOnlyPositionsNames.joinToString(",\n")
    )

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = onlyPickUpPositionsText,
                style = Typography.SmallTextStyle.copy(color = Colors.WhiteTransparent75),
            )
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))
            Text(
                text = stringResource(R.string.remove_pickup_only_from_cart),
                style = Typography.SmallTextStyle.copy(color = Colors.ErrorRed),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onRemovePickupOnly)
            )

        }
    }
}
