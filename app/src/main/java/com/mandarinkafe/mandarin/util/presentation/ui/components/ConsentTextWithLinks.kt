package com.mandarinkafe.mandarin.util.presentation.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.core.net.toUri
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun ConsentTextWithLinks(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val prefix = stringResource(id = R.string.consent_prefix) // "Соглашаюсь с"
    val privacy = stringResource(id = R.string.consent_privacy) // "политикой конфиденциальности"
    val agreement =
        stringResource(id = R.string.consent_agreement) // "пользовательским соглашением"
    val linkStyle = TextLinkStyles(
        SpanStyle(
            fontSize = Dimens.TextSizeSmall11,
            fontWeight = FontWeight.Light,
            color = Colors.WhiteTransparent75,
            textDecoration = TextDecoration.Underline,

            )
    )
    val privacyUrl = stringResource(id = R.string.privacy_policy_url)
    val agreementUrl = stringResource(id = R.string.user_agreement_url)
    val text = buildAnnotatedString {
        append("$prefix ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "PRIVACY",
                linkInteractionListener = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, privacyUrl.toUri()))
                },
                styles = linkStyle
            )
        ) { append(privacy) }

        append(" и ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "AGREEMENT",
                linkInteractionListener = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, agreementUrl.toUri()))
                },
                styles = linkStyle
            )
        ) { append(agreement) }

    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        BasicText(
            text = text,
            style = Typography.ConsentSmallTextStyle,
        )
    }
}