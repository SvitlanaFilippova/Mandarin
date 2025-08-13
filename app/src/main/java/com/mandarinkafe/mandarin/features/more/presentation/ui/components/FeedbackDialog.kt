package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.MaskVisualTransformation
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConsentTextWithLinks
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun FeedbackDialog(
    onDismissRequest: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val name = state.name
    val phone = state.phone
    val email = state.email
    val message = state.message
    val needFeedback = state.needFeedback

    val onEvent = viewModel::onEvent

    // Локальное состояние ошибки
    var isError by remember { mutableStateOf(false) }

    // Валидация
    val isMessageValid = message.isNotBlank()
    val isContactValid =
        !needFeedback || phone.length == Constants.VALID_PHONE_LENGTH || email.isNotBlank()

    val isFormValid = isMessageValid && isContactValid
    val mask = MaskVisualTransformation(stringResource(R.string.phone_mask))

    val colors = TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.ErrorRed,
        errorContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.more_message_manager)) },
        text = {
            Column {
                MyTextField(
                    value = name,
                    labelRes = R.string.your_name,
                    onValueChange = { onEvent(FeedbackEvent.SetName(it)) },
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))
                MyTextField(
                    value = phone,
                    labelRes = R.string.your_phone,
                    isError = !isContactValid && isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    onValueChange = { onEvent(FeedbackEvent.SetPhone(it)) },
                    visualTransformation = mask,

                    placeholder = {
                        Text(
                            text = stringResource(R.string.phone_placeholder),
                            style = Typography.RegularLightTextStyle
                        )
                    },
                    prefix = {
                        Text(
                            text = stringResource(R.string.phone_prefix),
                            style = Typography.RegularTextStyle
                        )
                    },
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                MyTextField(
                    value = email,
                    labelRes = R.string.your_email,
                    isError = !isContactValid && isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = { onEvent(FeedbackEvent.SetEmail(it)) },
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                MyTextField(
                    value = message,
                    onValueChange = { onEvent(FeedbackEvent.SetMessage(it)) },
                    labelRes = R.string.your_message,
                    isError = !isMessageValid && isError,
                    minLines = 4,
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                CheckboxWithTextRow(
                    checked = needFeedback,
                    labelRes = R.string.i_need_feedback,
                    onCheckedChange = { onEvent(FeedbackEvent.SetNeedFeedback(it)) }
                )
                Spacer(Modifier.height(Dimens.MarginSmall8))

                if (needFeedback && !isContactValid && isError) {
                    Text(
                        text = stringResource(R.string.contacts_are_required),
                        style = Typography.ErrorTextStyle,
                        modifier = Modifier.padding(start = Dimens.MarginStandard16)
                    )
                    Spacer(Modifier.height(Dimens.MarginSmall8))
                }
                ConsentTextWithLinks(
                    modifier = Modifier.padding(start = Dimens.MarginStandard16),
                    buttonName = stringResource(R.string.send)
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onEvent(FeedbackEvent.SubmitForm)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text(stringResource(R.string.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

