package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.more.presentation.models.getStoreIcon
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.DevFeedbackDialog
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberAboutViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.StoreLinkButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.OpenUrl
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
) {
    val viewModel = rememberAboutViewModel()
    val state by viewModel.state.collectAsState()
    val aboutMainText = stringResource(MR.strings.about_main_text)
    val askFeedbackText = stringResource(MR.strings.ask_feedback)
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.MarginStandard16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.about_title),
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

        Text(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            text = stringResource(MR.strings.message_developer),
            style = Typography.RegularTextStyle,
        )

        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.MarginSmall8),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modifier = Modifier
                .weight(1f)
                .padding(Dimens.MarginSmall8)

            DevFeedbackLink(
                modifier = modifier,
                onClick = {
                    showDialog = true
                }
            )

            StoreLinkButton(
                icon = painterResource(MR.images.ic_telegram),
                label = stringResource(MR.strings.telegram_label),
                url = stringResource(MR.strings.telegram_url),
                modifier = modifier
            )

        }

        Spacer(modifier = Modifier.height(Dimens.MarginBig32))

        // Ссылки на сторы, чтобы удобно было оставить отзыв
        Text(
            text = askFeedbackText,
            style = Typography.RegularLightTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val modifierForButton =
                Modifier.fillMaxWidth().padding(horizontal = Dimens.MarginStandard16)

            state.appStores.forEach { appStore ->
                val iconResource = getStoreIcon(appStore.storeId)
                iconResource?.let { icon ->
                    StoreLinkButton(
                        icon = painterResource(icon),
                        url = appStore.url,
                        label = appStore.label,
                        modifier = modifierForButton
                    )
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))


        Column(
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            // Время и дата последнего обновления меню из iiko
            state.lastUpdated?.let {
                Text(
                    text = stringResource(MR.strings.menu_last_updated_text, it),
                    style = Typography.SmallTextStyle,
                    color = Colors.LightGrey
                )
            }

            state.versionName?.let {
                // Версия приложения
                Text(
                    text = stringResource(MR.strings.version_text, it),
                    style = Typography.SmallTextStyle,
                    color = Colors.LightGrey
                )
            }
        }

    }

    if (showDialog) {
        DevFeedbackDialog(
            onDismissRequest = { showDialog = false },
        )
    }
}

@Composable
private fun DevFeedbackLink(
    onClick: () -> Unit,
    modifier: Modifier,
) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(
            horizontal = Dimens.MarginSmall8,
            vertical = Dimens.MarginSmall8
        )
    ) {
        Icon(
            painter = painterResource(MR.images.ic_email),
            contentDescription = null,
            tint = Colors.WhiteTransparent75
        )
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        Text(
            text = stringResource(MR.strings.dev_feedback_label),
        )
    }
}