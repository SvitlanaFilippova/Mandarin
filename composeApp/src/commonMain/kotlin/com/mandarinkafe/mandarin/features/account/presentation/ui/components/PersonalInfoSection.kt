package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PersonalInfoSection(
    phone: String,
    nameQuery: String,
    onNameEntered: (String) -> Unit,
    onPhoneClick: () -> Unit,
    saveNameNow: () -> Unit,
    showNameChangeButtons: Boolean,
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
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            MyTextField(
                value = nameQuery,
                labelRes = MR.strings.your_name,
                onValueChange = { onNameEntered(it) },
                trailingIcon = if (showNameChangeButtons) {
                    {
                        Row {
                            if (nameQuery.isNotEmpty()) {
                                IconButton(onClick = { onNameEntered("") }) {
                                    Icon(
                                        painter = painterResource(MR.images.ic_close),
                                        contentDescription = stringResource(MR.strings.clear_text),
                                        tint = Colors.WhiteTransparent75
                                    )
                                }
                                Spacer(modifier = Modifier.size(Dimens.MarginSuperSmall4))
                            }
                            IconButton(onClick = { saveNameNow() }) {
                                Icon(
                                    painter = painterResource(MR.images.ic_check),
                                    contentDescription = stringResource(MR.strings.ok),
                                    tint = Colors.Green
                                )
                            }
                        }
                    }
                } else {
                    {
                        Box(modifier = Modifier) // заглушка. Если текущее имя полностью совпадает с сохраннёным - не показываем ничего

                    }
                }
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
                        style = Typography.RegularLightTextStyle
                    )
                },
                prefix = {
                    Text(
                        text = stringResource(MR.strings.phone_prefix),
                        style = Typography.RegularTextStyle,
                        color = Colors.LightGrey
                    )
                },
                onClick = onPhoneClick
            )
        }
    }
}