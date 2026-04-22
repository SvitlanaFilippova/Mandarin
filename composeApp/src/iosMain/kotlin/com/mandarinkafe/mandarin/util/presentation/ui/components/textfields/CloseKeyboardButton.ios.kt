package com.mandarinkafe.mandarin.util.presentation.ui.components.textfields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun CloseKeyboardButton(isFocused: Boolean) {
    val focusManager = LocalFocusManager.current

    AnimatedVisibility(
        visible = isFocused,
        enter = slideInHorizontally { it },
        exit = shrinkOut() + slideOutHorizontally { it }
    ) {
        IconButton(onClick = {
            focusManager.clearFocus()
        }) {
            Icon(
                painterResource(MR.images.ic_close),
                contentDescription = stringResource(MR.strings.hide_keyboard),
            )
        }
    }
}