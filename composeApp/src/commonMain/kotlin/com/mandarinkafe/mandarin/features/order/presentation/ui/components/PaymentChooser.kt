package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.models.toUI
import dev.icerock.moko.resources.compose.stringResource

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
            text = stringResource(MR.strings.payment_type),
            style = style,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            paymentTypes.toUI().forEach { item ->
                OrderTypeChooserVerticalItem(
                    modifier = Modifier.weight(1f)
                        .clickable(
                            onClick = { onPaymentTypeSelected(item) },
                            role = Role.Button
                        ),
                    label = item.nameRes,
                    icon = item.iconRes,
                    selected = chosen?.code == item.code,
                    isError = isError && chosen == null
                )
            }
        }

        AnimatedVisibility(
            visible = showChangeInput,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Spacer(Modifier.height(Dimens.MarginSmall8))

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
