package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.BaseFeedbackDialog

@Composable
fun FeedbackDialog(
    onDismissRequest: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val successTitle = stringResource(R.string.message_sent_successfully)

    with(state) {
        BaseFeedbackDialog(
            titleRes = R.string.more_message_manager,
            onDismissRequest = onDismissRequest,
            effectFlow = viewModel.effect,
            onEvent = viewModel::onEvent,
            submitEvent = FeedbackEvent.SubmitForm,
            mapEffect = { effect ->
                when (effect) {
                    is FeedbackContract.FeedbackEffect.ShowError -> effect.message to false
                    is FeedbackContract.FeedbackEffect.ShowSuccess ->
                        successTitle to true
                }
            },
            name = name,
            phone = phone,
            email = email,
            message = message,
            needAnswer = needAnswer,
            isContactValid = isContactValid,
            onNameChange = { viewModel.onEvent(FeedbackEvent.SetName(it)) },
            onPhoneChange = { viewModel.onEvent(FeedbackEvent.SetPhone(it)) },
            onEmailChange = { viewModel.onEvent(FeedbackEvent.SetEmail(it)) },
            onMessageChange = { viewModel.onEvent(FeedbackEvent.SetMessage(it)) },
            onSetNeedFeedback = { viewModel.onEvent(FeedbackEvent.SetNeedFeedback(it)) },
            isFormValid = isFormValid,
            isLoading = isLoading
        )
    }
}