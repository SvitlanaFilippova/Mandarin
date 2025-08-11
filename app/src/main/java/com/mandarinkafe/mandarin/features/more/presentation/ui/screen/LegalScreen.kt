package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
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

@Composable
fun LegalScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        val context = LocalContext.current
        val cannotOpenToast = stringResource(id = R.string.cannot_open_link)
        val privacyLabel = stringResource(id = R.string.more_privacy_policy)
        val privacyUrl = stringResource(id = R.string.privacy_policy_url)
        val userAgreementLabel = stringResource(id = R.string.more_user_agreement)
        val userAgreementUrl = stringResource(id = R.string.user_agreement_url)
        val personalDataLabel = stringResource(id = R.string.more_personal_data_agreement)
        val personalDataUrl = stringResource(id = R.string.user_agreement_url)
        val yandexMapsLabel = stringResource(id = R.string.more_yandex_maps_terms)
        val yandexMapsUrl = stringResource(id = R.string.yandex_maps_terms_url)

        fun openUrl(url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, cannotOpenToast, Toast.LENGTH_SHORT).show()
            }
        }

        MenuItem(title = privacyLabel, onClick = { openUrl(privacyUrl) })
        MenuItem(title = userAgreementLabel, onClick = { openUrl(userAgreementUrl) })
        MenuItem(title = personalDataLabel, onClick = { openUrl(personalDataUrl) })
        MenuItem(title = yandexMapsLabel, onClick = { openUrl(yandexMapsUrl) })
    }
}