package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType

@Composable
fun DeliveryTypeTitle(
    chosen: DeliveryType?,
    isError: Boolean,
) {
    val style = if (isError && chosen == null) {
        Typography.RegularTextStyle.copy(color = Colors.Red)
    } else {
        Typography.RegularTextStyle
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4),
        text = stringResource(R.string.delivery_type),
        style = style,
    )
}