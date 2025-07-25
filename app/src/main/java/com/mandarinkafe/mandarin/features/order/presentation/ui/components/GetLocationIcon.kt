package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun GetLocationIcon(onClick: () -> Unit = {}, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            modifier = Modifier.padding(Dimens.MarginSuperSmall4),
            painter = painterResource(R.drawable.ic_pin_with_home),
            contentDescription = stringResource(id = R.string.street_and_building),
            tint = Colors.White
        )
    }
}