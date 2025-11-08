package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
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
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CallInstructionWithClickableLink(
    userPhone: String,
    phoneToCall: String,
    onPhoneClick: () -> Unit,
) {
    val linkStyle = TextLinkStyles(
        SpanStyle(
            fontSize = Dimens.TextSizeRegular14,
            fontWeight = FontWeight.Light,
            color = Colors.Orange,
            textDecoration = TextDecoration.Underline,
        )
    )
    val prefix = stringResource(
        MR.strings.verification_by_phone_instruction_prefix,
        userPhone.formatPhoneNumberForUi()
    )
    val end = stringResource(MR.strings.verification_by_phone_instruction_end)

    val text = buildAnnotatedString {
        append("$prefix ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "PHONE",
                linkInteractionListener = { onPhoneClick() },
                styles = linkStyle
            )
        ) { append(phoneToCall.formatPhoneNumberForUi()) }

        append(" $end")

    }

    BasicText(
        text = text,
        style = Typography.RegularLightTextStyle
    )
}