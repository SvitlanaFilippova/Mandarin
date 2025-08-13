package com.mandarinkafe.mandarin.util.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.models.UiError
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun PlaceholderScreen(
    error: UiError?,
    onRetryClick: () -> Unit = { },
    onCallClick: () -> Unit = { }
) {
    error?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.MarginStandard16)
                .background(Colors.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(error.imgRes),
                contentDescription = stringResource(error.msgRes),
                modifier = Modifier
                    .width(Dimens.PlaceholderImageSize200)
                    .padding(bottom = Dimens.MarginStandard16)
            )

            Text(
                text = stringResource(error.msgRes),
                style = Typography.PlaceholderTitleStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(Dimens.MarginSmall8)
            )

            if (error.extraMsgRes != null) {
                Text(
                    text = stringResource(error.extraMsgRes),
                    style = Typography.RegularTextStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.MarginStandard16)
                )
            }

            if (error.needRetry) {
                // Кнопка "Обновить"
                Button(
                    onClick = onRetryClick,
                    shape = RoundedCornerShape(Dimens.CornerRadius8),
                    modifier = Modifier
                        .width(Dimens.ButtonPlaceholderSize200),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Colors.White,
                        containerColor = Colors.Orange
                    )
                ) {
                    Text(text = stringResource(id = R.string.placeholder_retry))

                }

                Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

                // Кнопка "Позвонить"
                Button(
                    onClick = onCallClick,
                    shape = RoundedCornerShape(Dimens.CornerRadius8),
                    modifier = Modifier.width(Dimens.ButtonPlaceholderSize200),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Colors.White,
                        containerColor = Colors.Orange
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
                    Text(text = stringResource(id = R.string.placeholder_call))
                }

            }
        }
    }
}