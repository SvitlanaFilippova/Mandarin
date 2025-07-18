package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.presentation.ui.components.RadiobuttonWithTextRow

@Composable
fun PaymentChooser(
    chosen: PaymentType?,
    changeAmount: String,
    isError: Boolean,
    onPaymentTypeSelected: (PaymentType) -> Unit,
    onChangeEntered: (String) -> Unit,
) {
    val paymentTypes = remember { PaymentType.entries.toList() }
    val showChangeInput by remember(chosen) { mutableStateOf(chosen == PaymentType.CASH) }
    val style = if (isError && chosen == null) {
        Typography.RegularTextStyle.copy(color = Colors.ErrorRed)
    } else {
        Typography.RegularTextStyle
    }

    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.MarginSuperSmall4),
            text = stringResource(R.string.payment_type),
            style = style,
        )
        paymentTypes.forEach { item ->
            RadiobuttonWithTextRow(
                label = stringResource(item.nameRes),
                selected = chosen == item,
                onItemSelected = { onPaymentTypeSelected(item) }
            )
        }

        if (showChangeInput) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .padding(Dimens.MarginStandard16),
                    text = stringResource(R.string.payment_change),
                    style = Typography.RegularTextStyle
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = changeAmount,

                    shape = RoundedCornerShape(Dimens.CornerRadius8),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.payment_default_value),
                            style = Typography.RegularLightTextStyle
                        )
                    },
                    suffix = {
                        Text(
                            text = stringResource(R.string.payment_rub),
                            style = Typography.RegularLightTextStyle
                        )
                    },
                    onValueChange = { onChangeEntered(it) },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Colors.Orange,
                        focusedTextColor = Colors.White,
                        focusedContainerColor = Colors.DarkGrey,
                        focusedIndicatorColor = Colors.Orange,
                        unfocusedTextColor = Colors.White,
                        unfocusedContainerColor = Colors.DarkGrey,
                        unfocusedIndicatorColor = Colors.Transparent,
                    ),
                )
            }
        }
    }
}