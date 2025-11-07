package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.MaskVisualTransformation
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PersonalInfoSection(
    phone: String,
    nameQuery: String,
    onNameEntered: (String) -> Unit,
) {
    val mask = MaskVisualTransformation(stringResource(MR.strings.phone_mask))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(Dimens.MarginStandard16)
        ) {
            Text(
                text = stringResource(MR.strings.personal_info),
                style = Typography.RegularTextStyle,
                fontWeight = FontWeight.Bold
            )

            MyTextField(
                value = nameQuery,
                labelRes = MR.strings.your_name,
                onValueChange = { onNameEntered(it) }
            )

            // Номер телефона, к которому привязан аккаунт
            MyTextField(
                value = phone,
                enabled = false,
                labelRes = MR.strings.your_phone,
                visualTransformation = mask,
                placeholder = {
                    Text(
                        text = stringResource(MR.strings.phone_placeholder),
                        style = com.mandarinkafe.mandarin.core.presentation.theme.Typography.RegularLightTextStyle
                    )
                },
                prefix = {
                    Text(
                        text = stringResource(MR.strings.phone_prefix),
                        style = Typography.RegularTextStyle,
                        color = Colors.LightGrey
                    )
                }
            )
        }
    }
}