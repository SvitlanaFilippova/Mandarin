package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.stringResource

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
                        TextButton(
                            onClick = { snackbarData.performAction() }
                        ) {
                            Text(actionLabel)
                        }
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
