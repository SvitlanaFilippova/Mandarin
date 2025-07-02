package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.additionals.AddsCategoryTabsRow

@Composable
fun AddsHeader(
    selectedTabIndex: Int,
    categories: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Text(
        text = stringResource(id = R.string.adds),
        modifier = Modifier.padding(
            start = Dimens.MarginSmall8,
            top = Dimens.MarginBig24,
            bottom = Dimens.MarginSmall8
        ),
        style = Typography.TitleStyle
    )
    // Категории добавок
    AddsCategoryTabsRow(
        categories = categories,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { index ->
            onTabSelected(index)
        }
    )

}

