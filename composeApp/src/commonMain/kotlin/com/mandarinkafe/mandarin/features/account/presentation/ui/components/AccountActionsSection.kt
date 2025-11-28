package com.mandarinkafe.mandarin.features.account.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AccountActionsSection(
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.padding(Dimens.MarginSmall8),
        verticalArrangement = spacedBy(Dimens.MarginSuperSmall4)
    ) {
        // Кнопка "Выйти из аккаунта"
        TextButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            border = BorderStroke(width = Dimens.Border1, color = Colors.LightGrey),
            modifier = modifier
                .fillMaxWidth(),
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(end = Dimens.MarginSmall8),
                painter = painterResource(MR.images.ic_logout),
                contentDescription = null,
                tint = Colors.LightGrey
            )
            Text(
                text = stringResource(MR.strings.logout_from_account),
                style = Typography.RegularTextStyle,
                color = Colors.LightGrey
            )
        }

        // Кнопка "Удалить аккаунт"
        TextButton(
            onClick = onDeleteAccountClick,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            border = BorderStroke(width = Dimens.Border1, color = Colors.Red),
        ) {
            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24)
                    .padding(end = Dimens.MarginSmall8),
                painter = painterResource(MR.images.ic_delete),
                contentDescription = null,
                tint = Colors.Red
            )
            Text(
                text = stringResource(MR.strings.delete_account),
                style = Typography.RegularTextStyle,
                color = Colors.Red
            )
        }
    }
}

