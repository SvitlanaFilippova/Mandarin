package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun MandarinDeliveryInfo() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.mandarin_info),
        textAlign = TextAlign.Center,
        style = Typography.MealTitleStyle
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.mandarin_info_when),
        textAlign = TextAlign.Center,
        style = Typography.MealTitleStyle,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(Dimens.MarginSmall8))
}