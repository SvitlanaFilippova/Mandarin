package com.mandarinkafe.mandarin.util

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
actual fun Modifier.bottomSheetContentModifier(): Modifier {
    return this.fillMaxHeight().padding(bottom = Dimens.MarginStandard16)
}

@Composable
actual fun Modifier.bottomSheetHeaderModifier(): Modifier {
    return this.padding(top = Dimens.MarginStandard16)
}