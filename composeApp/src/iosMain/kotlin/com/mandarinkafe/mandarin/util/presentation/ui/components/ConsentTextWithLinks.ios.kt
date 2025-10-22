package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.stringResource
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun ConsentTextWithLinks(modifier: Modifier, buttonName: String?) {
    val finalButtonName = if (buttonName == null) "" else " \"$buttonName\""
    val prefix = stringResource(MR.strings.consent_prefix, finalButtonName)
    val privacy = stringResource(MR.strings.consent_privacy)
    val agreement = stringResource(MR.strings.consent_agreement)
    val linkStyle = TextLinkStyles(
        SpanStyle(
            fontSize = Dimens.TextSizeSmall11,
            fontWeight = FontWeight.Light,
            color = Colors.WhiteTransparent75,
            textDecoration = TextDecoration.Underline,
        )
    )
    val privacyUrl = stringResource(MR.strings.privacy_policy_url)
    val agreementUrl = stringResource(MR.strings.user_agreement_url)
    val text = buildAnnotatedString {
        append("$prefix ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "PRIVACY",
                linkInteractionListener = {
                    UIApplication.sharedApplication.openURL(
                        NSURL(string = privacyUrl),
                        mapOf<Any?, Any>()
                    ) { success ->
                        // URL открыт
                    }
                },
                styles = linkStyle
            )
        ) { append(privacy) }

        append(" и ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "AGREEMENT",
                linkInteractionListener = {
                    UIApplication.sharedApplication.openURL(
                        NSURL(string = agreementUrl),
                        mapOf<Any?, Any>()
                    ) { success ->
                        // URL открыт
                    }
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


