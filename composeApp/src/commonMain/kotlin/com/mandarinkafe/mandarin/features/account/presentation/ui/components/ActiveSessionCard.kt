package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import dev.icerock.moko.resources.compose.painterResource
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
        Icon(
            modifier = Modifier
                .padding(end = Dimens.MarginSmall8)
                .size(Dimens.IconSize24),
            painter = painterResource(MR.images.ic_smartphone),
            contentDescription = null,
            tint = if (session.isCurrent) Colors.Orange else Colors.White
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = session.deviceName ?: stringResource(MR.strings.unknown_device),
                style = Typography.RegularTextStyle,
                color = if (session.isCurrent) Colors.Orange else Colors.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (session.isCurrent) {
                Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall2))
                Text(
                    text = stringResource(MR.strings.current_session),
                    style = Typography.SmallLightTextStyle,
                    color = Colors.Orange,
                    maxLines = 1,
                )
            } else {
                session.createdAt?.let { createdAt ->
                    Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall4))
                    Text(
                        text = stringResource(MR.strings.login_date, createdAt),
                        style = Typography.SmallLightTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        if (!session.isCurrent) {
            TextButton(onClick = onRevokeSession) {
                Icon(
                    modifier = Modifier
                        .size(Dimens.IconSize24)
                        .padding(end = Dimens.MarginSmall8),
                    painter = painterResource(MR.images.ic_logout),
                    contentDescription = null,
                    tint = Colors.Red
                )

                Text(
                    text = stringResource(MR.strings.logout),
                    style = Typography.RegularLightTextStyle,
                    color = Colors.Red
                )
            }
        }
    }
}


