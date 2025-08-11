package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.SectionHeader
import com.mandarinkafe.mandarin.navigation.extensions.navigateOrdersHistory
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAboutScreen
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSavedAddresses

@Composable
fun MoreMenuScreen(navController: NavHostController) {
    val context = LocalContext.current

    val sectionUserData = stringResource(id = R.string.more_section_user_data)
    val ordersHistory = stringResource(id = R.string.more_orders_history)
    val savedAddresses = stringResource(id = R.string.more_saved_addresses)

    val sectionLegal = stringResource(id = R.string.more_section_legal_info)
    val privacyLabel = stringResource(id = R.string.more_privacy_policy)
    val privacyUrl = stringResource(id = R.string.privacy_policy_url)
    val userAgreementLabel = stringResource(id = R.string.more_user_agreement)
    val userAgreementUrl = stringResource(id = R.string.user_agreement_url)
    val personalDataLabel = stringResource(id = R.string.more_personal_data_agreement)
    val personalDataUrl = stringResource(id = R.string.more_personal_data_agreement_url)
    val yandexMapsLabel = stringResource(id = R.string.more_yandex_maps_terms)
    val yandexMapsUrl = stringResource(id = R.string.yandex_maps_terms_url)

    val aboutTitle = stringResource(id = R.string.about_title)
    val cannotOpenToast = stringResource(id = R.string.cannot_open_link)

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, cannotOpenToast, Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        item {
            SectionHeader(title = sectionUserData)
            MenuItem(title = ordersHistory, iconRes = R.drawable.ic_history, onClick = {
                navController.navigateOrdersHistory()
            })
            MenuItem(title = savedAddresses, iconRes = R.drawable.ic_cottage, onClick = {
                navController.navigateToSavedAddresses()
            })
        }

        item { Spacer(Modifier.size(Dimens.MarginBig32)) }

        item {
            SectionHeader(title = sectionLegal)
            MenuItem(title = privacyLabel, onClick = { openUrl(privacyUrl) })
            MenuItem(title = userAgreementLabel, onClick = { openUrl(userAgreementUrl) })
            MenuItem(title = personalDataLabel, onClick = { openUrl(personalDataUrl) })
            MenuItem(title = yandexMapsLabel, onClick = { openUrl(yandexMapsUrl) })
        }

        item {
            Spacer(Modifier.size(Dimens.MarginBig32))
            MenuItem(title = aboutTitle, onClick = {
                navController.navigateToAboutScreen()
            })
        }
    }
}