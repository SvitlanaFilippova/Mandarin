package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SuccessAuth() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(MR.images.ic_check),
            contentDescription = null,
            tint = Colors.Green
        )

        Text(
            text = stringResource(
                MR.strings.phone_verified
            ),
            style = Typography.RegularTextStyle
        )
    }
}