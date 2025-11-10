package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.auth.domain.models.ActiveSession
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ActiveSessionsSection(
    isLoading: Boolean,
    sessions: List<ActiveSession>,
    onRevokeSession: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(Dimens.MarginStandard16)
        ) {
            Text(
                text = stringResource(MR.strings.active_devices),
                style = Typography.RegularTextStyle,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            when {
                isLoading && sessions.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.MarginStandard16),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Colors.Orange)
                    }
                }

                sessions.isEmpty() -> {
                    TooltipText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.MarginStandard16),
                        text = stringResource(MR.strings.no_active_sessions)
                    )
                }

                else -> {
                    sessions.forEachIndexed { index, session ->
                        ActiveSessionCard(
                            session = session,
                            onRevokeSession = {
                                onRevokeSession(session.tokenId)
                            }
                        )

                        if (index < sessions.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                thickness = Dimens.DividerHeight1,
                                color = Colors.LightGrey.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}