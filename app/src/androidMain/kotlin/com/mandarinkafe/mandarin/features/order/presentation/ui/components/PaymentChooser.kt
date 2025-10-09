package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.toUI
import com.mandarinkafe.mandarin.util.presentation.ui.components.RadiobuttonWithTextRow

@Composable
fun PaymentChooser(
    chosen: UiPaymentType?,
    changeAmount: String,
    isError: Boolean,
    noChange: Boolean,
    onNoChangeToggled: (Boolean) -> Unit,
    onPaymentTypeSelected: (UiPaymentType) -> Unit,
    onChangeEntered: (String) -> Unit,
    paymentTypes: List<PaymentType>,
) {
    val showChangeInput by remember(chosen) { mutableStateOf(chosen == UiPaymentType.CASH) }
    val style = if (isError && chosen == null) {
        Typography.RegularTextStyle.copy(color = Colors.Red)
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
        paymentTypes.toUI().forEach { item ->
            RadiobuttonWithTextRow(
                label = stringResource(item.nameRes),
                selected = chosen?.code == item.code,
                onItemSelected = { onPaymentTypeSelected(item) }
            )
        }

        AnimatedVisibility(
            visible = showChangeInput,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ChangeInfo(
                noChange = noChange,
                onNoChangeToggled = { onNoChangeToggled(it) },
                changeAmount = changeAmount,
                onChangeEntered = { onChangeEntered(it) }
            )
        }
        Spacer(Modifier.height(Dimens.MarginSmall8))
    }
}