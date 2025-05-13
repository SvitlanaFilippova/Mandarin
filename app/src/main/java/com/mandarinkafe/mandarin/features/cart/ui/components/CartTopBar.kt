package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun CartTopBar(
    onBackClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ToolbarHeadHeight56)
    ) {

        // Кнопка возврата
        Icon(
            painter = painterResource(R.drawable.ic_close),
            tint = Colors.White,
            contentDescription = stringResource(R.string.back),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .clickable(onClick = onBackClick)
                .padding(Dimens.MarginStandard16)
        )
        // Логотип
        Image(
            painter = painterResource(R.drawable.logo_text_mandarin),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(Dimens.MarginSmall8)
        )

        // Иконка звонка
        Icon(
            painter = painterResource(R.drawable.ic_call),
            tint = Colors.White,
            contentDescription = stringResource(R.string.placeholder_call),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .clickable(onClick = onCallClick)
                .padding(Dimens.MarginStandard16)
        )
    }
}