package com.mandarinkafe.mandarin.navigation.bottomnav.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.TextUnit
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun rememberAdaptiveBottomNavFontSize(): TextUnit {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    return remember(windowInfo.containerSize.width) {
        val widthPx = windowInfo.containerSize.width
        val widthDp = with(density) { widthPx.toDp() }

        when {
            widthDp < Dimens.BottomNavBreakpointSmall -> Dimens.TextSizeSuperSmall9
            widthDp < Dimens.BottomNavBreakpointMedium -> Dimens.TextSizeSuperSmall10
            widthDp < Dimens.BottomNavBreakpointLarge -> Dimens.TextSizeSmall11
            else -> Dimens.TextSizeSmall12
        }
    }
}
