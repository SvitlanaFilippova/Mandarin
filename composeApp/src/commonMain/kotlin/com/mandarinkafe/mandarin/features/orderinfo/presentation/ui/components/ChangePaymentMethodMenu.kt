package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.toUI
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChangePaymentMethodMenu(
    expanded: Boolean,
    availablePaymentTypes: List<PaymentType>,
    currentPaymentMethodCode: String?,
    onPaymentMethodSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = Colors.AppBlack
    ) {
        availablePaymentTypes.toUI().forEach { uiPaymentType ->
            val isChecked = currentPaymentMethodCode == uiPaymentType.code
            DropdownMenuItem(
                text = { Text(text = stringResource(uiPaymentType.nameRes)) },
                onClick = {
                    onPaymentMethodSelected(uiPaymentType.code)
                },
                leadingIcon = {
                    Icon(
                        painterResource(uiPaymentType.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSize24),
                        tint = Colors.WhiteTransparent75
                    )
                },
                trailingIcon = {
                    if (isChecked) {
                        Icon(
                            painterResource(MR.images.ic_check),
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.IconSize24),
                            tint = Colors.WhiteTransparent75
                        )
                    }
                }
            )
        }
    }
}