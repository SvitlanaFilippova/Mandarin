package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.ContactLink
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun AboutScreen(onBackClick: () -> Boolean) {
    val appVersion: String = BuildConfig.VERSION_NAME
    val versionText = stringResource(id = R.string.version_text, appVersion)
    val aboutMainText = stringResource(id = R.string.about_main_text)
    stringResource(id = R.string.contact_title)
    val thanksText = stringResource(id = R.string.thanks_text)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.MarginStandard16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(id = R.string.about_title),
            onBackClick = { onBackClick() },
        )

        Spacer(modifier = Modifier.height(Dimens.MarginBig32))

        Text(
            modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
            text = aboutMainText,
            style = Typography.RegularLightTextStyle,
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.MarginSmall8),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactLink(
                url = stringResource(R.string.telegram_url),
                label = stringResource(id = R.string.telegram_label),
                iconRes = R.drawable.ic_telegram
            )

            ContactLink(
                url = stringResource(R.string.github_url),
                label = stringResource(id = R.string.github_label),
                iconRes = R.drawable.ic_github
            )
        }

        Spacer(modifier = Modifier.height(Dimens.MarginHuge80))

        // Спасибо
        Text(
            text = thanksText,
            style = Typography.RegularLightTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Версия приложения
        Text(
            text = versionText,
            style = Typography.SmallTextStyle,
            color = Colors.LightGrey
        )
        Spacer(modifier = Modifier.height(Dimens.MarginSmall8))
    }
}