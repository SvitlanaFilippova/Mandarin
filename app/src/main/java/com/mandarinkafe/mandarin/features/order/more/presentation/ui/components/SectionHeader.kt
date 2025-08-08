package com.mandarinkafe.mandarin.features.order.more.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun SectionHeader(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginStandard16)
    ) {
        Text(
            text = title.uppercase(),
            style = Typography.RegularLightTextStyle,
            color = Colors.LightGrey,
            modifier = Modifier
                .padding(vertical = Dimens.MarginStandard16)
        )
        HorizontalDivider(
            Modifier.height(Dimens.DividerHeight1),
        )
    }
}