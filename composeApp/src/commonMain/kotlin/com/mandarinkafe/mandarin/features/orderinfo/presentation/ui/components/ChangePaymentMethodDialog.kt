package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.toUI
import com.mandarinkafe.mandarin.util.presentation.ui.components.RadiobuttonWithTextRow
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChangePaymentMethodDialog(
    availablePaymentTypes: List<PaymentType>,
    currentPaymentMethodCode: String?,
    onPaymentMethodSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(MR.strings.payment_type)) },
        text = {
            Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.MarginSuperSmall4),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                availablePaymentTypes.toUI().forEach { uiPaymentType ->
                    RadiobuttonWithTextRow(
                        label = stringResource(uiPaymentType.nameRes),
                        selected = currentPaymentMethodCode?.equals(uiPaymentType.code, ignoreCase = true) == true,
                        onItemSelected = {
                            onPaymentMethodSelected(uiPaymentType.code)
                        }
                    )
                }
            }
        },
        containerColor = Colors.DarkGrey,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(MR.strings.cancel))
            }
        },
        dismissButton = null
    )
}

