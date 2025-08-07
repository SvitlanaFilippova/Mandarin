package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.MenuItem
import com.mandarinkafe.mandarin.features.more.presentation.ui.components.SectionHeader
import com.mandarinkafe.mandarin.navigation.extensions.navigateOrdersHistory
import com.mandarinkafe.mandarin.navigation.extensions.navigateToSavedAddresses

@Composable
fun MoreMenuScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens.MarginStandard16)
    ) {
        item {
            SectionHeader(title = "Ваши данные")
            MenuItem(title = "История заказов", iconRes = R.drawable.ic_history, onClick = {
                navController.navigateOrdersHistory()
            })
            MenuItem(title = "Сохранённые адреса", iconRes = R.drawable.ic_cottage, onClick = {
                navController.navigateToSavedAddresses()
            })
        }
        item { Spacer(Modifier.size(Dimens.MarginBig32)) }
        item {
            SectionHeader(title = "Юридическая информация")
            MenuItem(title = "Условия использования Яндекс Карт", onClick = { })
            MenuItem(title = "Политика конфиденциальности", onClick = { })
            MenuItem(title = "Пользовательское соглашение", onClick = { })
            MenuItem(title = "Соглашение об обработке персональных данных", onClick = { })
        }
    }
}

