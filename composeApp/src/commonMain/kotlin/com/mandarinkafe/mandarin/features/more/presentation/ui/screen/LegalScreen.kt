package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.OpenUrl
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun LegalScreen(onSharedEvent: (SharedContract.SharedEvent) -> Unit, onBackClick: () -> Unit) {

    val privacyLabel = stringResource(MR.strings.more_privacy_policy)
    val privacyUrl = stringResource(MR.strings.privacy_policy_url)
    val personalDataLabel = stringResource(MR.strings.more_personal_data_agreement)
    val personalDataUrl = stringResource(MR.strings.user_agreement_url)
    val yandexMapsLabel = stringResource(MR.strings.more_yandex_maps_terms)
    val yandexMapsUrl = stringResource(MR.strings.yandex_maps_terms_url)

    var urlToOpen by remember { mutableStateOf<String?>(null) }


    urlToOpen?.let { url ->
        OpenUrl(
            url = url,
            onFail = {
                onSharedEvent(
                    SharedContract.SharedEvent.ShowSnackbar(
                        messageRes = MR.strings.cannot_open_link
                    )
                )
            }
        )
        LaunchedEffect(Unit) {
            urlToOpen = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
    ) {
        ScreenTitleWithBackButton(
            name = stringResource(MR.strings.more_section_legal_info),
            onBackClick = { onBackClick() },
        )

        MenuItem(
            title = privacyLabel,
            onClick = {
                urlToOpen = privacyUrl
            }
        )
        MenuItem(
            title = personalDataLabel,
            onClick = {
                urlToOpen = personalDataUrl
            }
        )
        MenuItem(
            title = yandexMapsLabel,
            onClick = {
                urlToOpen = yandexMapsUrl
            }
        )
    }
}
