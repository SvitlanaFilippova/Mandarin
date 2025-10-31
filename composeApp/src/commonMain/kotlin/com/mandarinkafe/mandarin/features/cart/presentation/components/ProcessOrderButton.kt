package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.ButtonWithCircularProgressIndicator
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ProcessOrderButton(
    modifier: Modifier = Modifier,
    totalPrice: Int,
    proceedOrderIsLoading: Boolean,
    onClick: () -> Unit,
) {
    val modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = Dimens.MarginSmall8)
        .height(Dimens.BigButtonWithTextHeight)

    if (proceedOrderIsLoading) {
        ButtonWithCircularProgressIndicator(modifier = modifier)
    } else {
        Button(
            modifier = modifier,
            onClick = onClick,
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            colors = ButtonDefaults.buttonColors(
                containerColor = Colors.Orange,
                contentColor = Color.White,
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
            ) {
                Text(
                    text = stringResource(MR.strings.submit_order),
                    style = Typography.ToCartButtonBigStyle,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(MR.strings.cart_total_cost_template, totalPrice),
                    style = Typography.ToCartButtonBigStyle,
                )
            }
        }
    }
}
