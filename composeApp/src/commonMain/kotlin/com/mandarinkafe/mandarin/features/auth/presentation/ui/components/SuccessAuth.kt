package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.DialogContainer
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SuccessAuthDialog(onDismissRequest: () -> Unit) {
    DialogContainer(
        onDismissRequest = onDismissRequest,
    ) {
        Icon(
            modifier = Modifier.size(Dimens.IconHuge),
            painter = painterResource(MR.images.ic_check),
            contentDescription = null,
            tint = Colors.Green
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(
                MR.strings.phone_verified
            ),
            style = Typography.RegularTextStyle,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(Dimens.MarginBig32))
    }
}