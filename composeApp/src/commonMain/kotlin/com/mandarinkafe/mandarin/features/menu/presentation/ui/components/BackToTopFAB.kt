package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BackToTopFAB(
    modifier: Modifier,
    visible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
            .padding(Dimens.MarginStandard16)
    ) {
        SmallFloatingActionButton(
            onClick = { onClick() },
            containerColor = Colors.Orange,
        ) {
            Icon(
                painter = painterResource(MR.images.ic_keyboard_arrow_up),
                contentDescription = stringResource(MR.strings.back_to_top),
                tint = Colors.White
            )
        }
    }
}
