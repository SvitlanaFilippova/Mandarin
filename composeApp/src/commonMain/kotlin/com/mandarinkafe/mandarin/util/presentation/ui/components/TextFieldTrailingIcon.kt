package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TextFieldTrailingIcon(
    value: String,
    onClear: () -> Unit,
) {
    val action: () -> Unit = when {
        value.isNotEmpty() -> {
            { onClear() }
        }

        else -> {
            { }
        }
    }

    val iconRes = when {
        value.isNotEmpty() -> MR.images.ic_close
        else -> null
    }

    iconRes?.let {
        IconButton(onClick = action) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(MR.strings.clear_text),
                tint = Colors.LightGrey
            )
        }
    }
}