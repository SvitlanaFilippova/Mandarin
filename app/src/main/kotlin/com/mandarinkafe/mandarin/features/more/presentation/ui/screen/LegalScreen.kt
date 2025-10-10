package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun LegalScreen(onSharedEvent: (SharedEvent) -> Unit, onBackClick: () -> Boolean) {
    val context = LocalContext.current
    val cannotOpenLinkMessage = stringResource(id = R.string.cannot_open_link)
    val privacyLabel = stringResource(id = R.string.more_privacy_policy)
    val privacyUrl = stringResource(id = R.string.privacy_policy_url)
    val personalDataLabel = stringResource(id = R.string.more_personal_data_agreement)
    val personalDataUrl = stringResource(id = R.string.user_agreement_url)
    val yandexMapsLabel = stringResource(id = R.string.more_yandex_maps_terms)
    val yandexMapsUrl = stringResource(id = R.string.yandex_maps_terms_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(id = R.string.more_section_legal_info),
            onBackClick = { onBackClick() },
        )

        fun openUrl(url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                onSharedEvent(
                    SharedEvent.ShowSnackbar(
                        message = cannotOpenLinkMessage + ": ${e.message}"
                    )
                )
            }
        }

        MenuItem(title = privacyLabel, onClick = { openUrl(privacyUrl) })
        MenuItem(title = personalDataLabel, onClick = { openUrl(personalDataUrl) })
        MenuItem(title = yandexMapsLabel, onClick = { openUrl(yandexMapsUrl) })
    }
}
