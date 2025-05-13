package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.util.applyTypography

@Composable
fun CartPlaceholder(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Image(
            painter = painterResource(R.drawable.logo_orange),
            contentDescription = stringResource(R.string.logo_cafe),
            modifier = Modifier
                .width(Dimens.PlaceholderImageSize120)
                .padding(bottom = Dimens.MarginBig24)
        )

        Text(
            text = errorMessage.applyTypography(),
            color = Colors.White,
            style = Typography.RegularTextStyle
        )

    }
}