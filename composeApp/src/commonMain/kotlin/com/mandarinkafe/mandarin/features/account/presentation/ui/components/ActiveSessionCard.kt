package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ActiveSessionCard(
    modifier: Modifier = Modifier,
    session: ActiveSession,
    onRevokeSession: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSmall8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = session.deviceName ?: stringResource(MR.strings.unknown_device),
                style = Typography.RegularTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            session.createdAt?.let { createdAt ->
                Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall4))
                Text(
                    text = stringResource(MR.strings.login_date, createdAt),
                    style = Typography.MealSmallTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (session.isCurrent) {
                Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall2))
                Text(
                    text = stringResource(MR.strings.current_session),
                    style = Typography.MealSmallTextStyle,
                    color = Colors.Orange,
                    maxLines = 1,
                )
            }
        }

        if (!session.isCurrent) {
            TextButton(onClick = onRevokeSession) {
                Text(
                    text = stringResource(MR.strings.logout),
                    style = Typography.RegularTextStyle,
                    color = Colors.Red
                )
            }
        }
    }
}

