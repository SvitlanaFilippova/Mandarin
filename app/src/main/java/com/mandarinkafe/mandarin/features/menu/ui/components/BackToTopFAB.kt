package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun BackToTopFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = Colors.Orange.copy(alpha = 0.90f),
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = stringResource(R.string.back_to_top),
            tint = Colors.White
        )
    }
}
