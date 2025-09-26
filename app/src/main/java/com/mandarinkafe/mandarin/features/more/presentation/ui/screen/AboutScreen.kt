package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.DevFeedbackDialog
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.AboutViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun AboutScreen(
    onBackClick: () -> Boolean,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val versionText = stringResource(id = R.string.version_text, state.versionName ?: "")
    val lastUpdatedText =
        stringResource(id = R.string.menu_last_updated_text, state.lastUpdated ?: "")
    val revisionText = stringResource(id = R.string.menu_revision_text, state.revision ?: 0)

    val aboutMainText = stringResource(id = R.string.about_main_text)
    val thanksText = stringResource(id = R.string.thanks_text)
    var showDialog by remember { mutableStateOf(false) }

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

        Text(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            text = stringResource(R.string.message_developer),
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
                .padding(vertical = Dimens.MarginStandard16, horizontal = Dimens.MarginSmall8)

            DevFeedbackLink(
                modifier = modifier,
                onClick = {
                    showDialog = true
                }
            )

            ContactLink(
                modifier = modifier,
                url = stringResource(R.string.telegram_url),
                label = stringResource(id = R.string.telegram_label),
                iconRes = R.drawable.ic_telegram
            )

        }

        Spacer(modifier = Modifier.height(Dimens.MarginBig32))

        // Спасибо
        Text(
            text = thanksText,
            style = Typography.RegularLightTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))


        Column(
            modifier = Modifier.padding(bottom = Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            // Время и дата последнего обновления меню из iiko
            Text(
                text = lastUpdatedText,
                style = Typography.SmallTextStyle,
                color = Colors.LightGrey
            )
            // Ревизия меню
            Text(
                text = revisionText,
                style = Typography.SmallTextStyle,
                color = Colors.LightGrey
            )
            // Версия приложения
            Text(
                text = versionText,
                style = Typography.SmallTextStyle,
                color = Colors.LightGrey
            )
        }

    }

    if (showDialog) {
        DevFeedbackDialog(
            onDismissRequest = { showDialog = false },
        )
    }
}

@Composable
private fun ContactLink(
    label: String,
    @DrawableRes iconRes: Int,
    url: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },

        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = Colors.WhiteTransparent75
        )
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        Text(text = label, style = Typography.RegularTextStyle)
    }
}

@Composable
private fun DevFeedbackLink(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = Colors.WhiteTransparent75
        )
        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
        Text(
            text = stringResource(R.string.dev_feedback_label),
            style = Typography.RegularTextStyle
        )
    }
}