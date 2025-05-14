package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun BackToTopFAB(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = Colors.Orange,
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = stringResource(R.string.back_to_top),
            tint = Colors.White
        )
    }
}
