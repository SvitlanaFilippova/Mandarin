package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.OpenUrl

@Composable
fun IconLinkButton(
    icon: Painter,
    label: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    var shouldOpenUrl by remember { mutableStateOf<Boolean?>(null) }

    shouldOpenUrl?.let {
        OpenUrl(url = url)
        LaunchedEffect(Unit) {
            shouldOpenUrl = null
        }
    }

    OutlinedButton(
        onClick = { shouldOpenUrl = true },
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(
            horizontal = Dimens.MarginSmall8,
            vertical = Dimens.MarginSmall8
        )
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        Text(label)
    }
}