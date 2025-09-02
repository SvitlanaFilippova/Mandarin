package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(
                    horizontal = Dimens.MarginStandard16,
                    vertical = Dimens.MarginBig32
                ),
                containerColor = Colors.White.copy(alpha = 0.9f),
                contentColor = Colors.AppBlack,
                action = {
                    // Кнопка действия
                    snackbarData.visuals.actionLabel?.let { actionLabel ->
                        ButtonWithText(
                            modifier = Modifier.padding(Dimens.MarginSmall8),
                            text = actionLabel,
                            onClick = { snackbarData.performAction() }
                        )
                    }
                },
                dismissAction = {
                    // Иконка закрытия для dismiss action
                    IconButton(
                        onClick = { snackbarData.dismiss() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Colors.DarkGrey
                        )
                    }
                }
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}