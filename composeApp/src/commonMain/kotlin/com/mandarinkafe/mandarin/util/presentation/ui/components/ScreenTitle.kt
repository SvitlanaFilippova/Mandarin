package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun ScreenTitle(
    modifier: Modifier = Modifier,
    name: String,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            text = name,
            style = Typography.TitleStyle,
        )
    }
}