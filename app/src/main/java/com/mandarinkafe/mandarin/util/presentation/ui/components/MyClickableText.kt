package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun MyClickableText(
    modifier: Modifier = Modifier,
    text: String? = null,
    textRes: Int? = null,
    onClick: () -> Unit
) {
    val resolvedText = when {
        text != null -> text
        textRes != null -> stringResource(id = textRes)
        else -> return
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .padding(Dimens.MarginSmall8),
        text = resolvedText,
        textAlign = TextAlign.Center,
        style = Typography.RegularTextStyle,
        color = Colors.Orange
    )
}