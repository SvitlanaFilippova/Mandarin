package com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConsentTextWithLinks
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <Effect, Event> BaseFeedbackDialog(
    title: StringResource,
    onDismissRequest: () -> Unit,
    effectFlow: Flow<Effect>,
    onEvent: (Event) -> Unit,
    submitEvent: Event,
    mapEffect: (Effect) -> Pair<String, Boolean>, // message, isSuccess
    message: String,
    needAnswer: Boolean,
    onMessageChange: (String) -> Unit,
    onSetNeedFeedback: (Boolean) -> Unit,
    isFormValid: Boolean,
    isLoading: Boolean,
) {
    var isError by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    stringResource(MR.strings.message_sent_successfully)

    val colors = TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.Red,
        errorContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(title)) },
        text = {
            FeedbackFields(
                colors = colors,
                message = message,
                needAnswer = needAnswer,
                onMessageChange = onMessageChange,
                onSetNeedFeedback = onSetNeedFeedback,
                isError = isError,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onEvent(submitEvent)
                    } else {
                        isError = true
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    MyCircularProgressIndicator(
                        modifier = Modifier
                            .size(Dimens.ProgressBarSmallSize),
                        strokeWidth = Dimens.ProgressBarStroke6,
                    )
                }
                Text(stringResource(MR.strings.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(MR.strings.cancel))
            }
        }
    )

    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                dialogMessage = null
                if (isSuccess) onDismissRequest()
            },
            confirmButton = {
                TextButton(onClick = {
                    dialogMessage = null
                    if (isSuccess) onDismissRequest()
                }) {
                    Text(stringResource(MR.strings.ok))
                }
            },
            title = { Text(stringResource(title)) },
            text = { Text(dialogMessage ?: "") }
        )
    }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            val (msg, success) = mapEffect(effect)
            dialogMessage = msg
            isSuccess = success
        }
    }
}

@Composable
private fun FeedbackFields(
    colors: TextFieldColors,
    onMessageChange: (String) -> Unit,
    onSetNeedFeedback: (Boolean) -> Unit,
    message: String,
    needAnswer: Boolean,
    isError: Boolean,
) {
    Column {
        MyTextField(
            value = message,
            onValueChange = { onMessageChange(it) },
            labelRes = MR.strings.your_message,
            isError = !message.isNotBlank() && isError,
            minLines = 4,
            colors = colors
        )

        Spacer(Modifier.height(Dimens.MarginSmall8))

        CheckboxWithTextRow(
            checked = needAnswer,
            text = stringResource(MR.strings.i_need_feedback),
            onCheckedChange = { onSetNeedFeedback(it) }
        )
        Spacer(Modifier.height(Dimens.MarginSmall8))

        ConsentTextWithLinks(
            buttonName = stringResource(MR.strings.send)
        )
    }
}
