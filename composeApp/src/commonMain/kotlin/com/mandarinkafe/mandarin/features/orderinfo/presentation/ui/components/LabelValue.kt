package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun LabelValue(label: String, value: String, valueColor: Color? = null) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(label)
        Value(value, color = valueColor)
    }
}

@Composable
fun Label(text: String) {
    Text(
        modifier = Modifier.padding(end = Dimens.MarginStandard16),
        textAlign = TextAlign.Center,
        text = text,
        style = Typography.RegularLightTextStyle.copy(color = Color.Gray)
    )
}

@Composable
fun Value(text: String, color: Color? = null) {
    Text(
        text = text,
        style = Typography.RegularTextStyle.copy(color = color ?: Color.Unspecified),
    )
}