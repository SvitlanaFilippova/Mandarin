package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
fun GetLocationIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = stringResource(id = R.string.get_address),
            tint = Colors.White
        )
    }
}