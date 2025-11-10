package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.PhoneField
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AskPhoneComponent(
    phoneQuery: String,
    isPhoneValid: Boolean,
    onValueChange: (String) -> Unit,
    onRequestAuth: () -> Unit,
) {
    var showError by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = showError && !isPhoneValid) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginSmall8),
            text = stringResource(MR.strings.enter_valid_phone),
            style = Typography.ErrorTextStyle,
            textAlign = TextAlign.Start
        )
    }
    PhoneField(
        value = phoneQuery,
        isError = showError && !isPhoneValid,
        onValueChange = { onValueChange(it) },
    )

    ButtonWithText(
        modifier = Modifier.width(Dimens.ButtonPlaceholderSize200)
            .padding(vertical = Dimens.MarginStandard16),
        text = stringResource(MR.strings.confirm),
        onClick = onRequestAuth,
        shouldBeActive = isPhoneValid,
        onMissingRequiredInfo = { showError = true },
    )
}