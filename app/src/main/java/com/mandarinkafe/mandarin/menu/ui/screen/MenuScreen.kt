package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.RVItem
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.models.mockMenuData
import com.mandarinkafe.mandarin.menu.ui.components.ItemMenuMeal
import kotlinx.coroutines.launch

@Preview
@Composable
fun MenuScreenPreview() {
    fun menuToMenuItems(menu: List<MealCategory>?): List<RVItem> {
        val menuItems = buildList<RVItem> {
            menu?.forEach { category ->
                if (!category.subCategories.isNullOrEmpty()) {
                    this += MenuRVItem.HeaderItem(
                        categoryName = category.name,
                        subCategoriesNames = buildList {
                            category.subCategories.forEach { this += it.name }
                        },
                        tabIcon = category.tabIcon
                    )

                    category.subCategories.forEach { subCategory ->
                        if (!subCategory.meals.isNullOrEmpty()) {
                            this += MenuRVItem.SubHeaderItem(
                                categoryName = subCategory.name
                            )
                            this += subCategory.meals.map { MenuRVItem.MealItem(meal = it) }
                        }
                    }
                } else {
                    if (!category.meals.isNullOrEmpty()) {
                        this += MenuRVItem.HeaderItem(
                            categoryName = category.name,
                            subCategoriesNames = null,
                            tabIcon = category.tabIcon
                        )
                        this += category.meals.map { MenuRVItem.MealItem(it) }
                    }
                }
            }
        }
        return menuItems
    }

    MenuScreen(menuToMenuItems(mockMenuData))

}

@Composable
fun MenuScreen(menuItems: List<RVItem>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()
        .background(Colors.AppBlack)

        ) {
        // Горизонтальный список категорий (табы)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginSmall8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            items(menuItems.filterIsInstance<MenuRVItem.HeaderItem>()) { headerItem ->
                TabItem(headerItem, isSelected = selectedCategory == headerItem.categoryName) {
                    selectedCategory = headerItem.categoryName
                    coroutineScope.launch {
                        val index = menuItems.indexOfFirst {
                            it is MenuRVItem.HeaderItem && it.categoryName == selectedCategory
                        }
                        if (index >= 0) {
                            listState.animateScrollToItem(index)
                        }
                    }
                }
            }
        }

        // Вертикальный список блюд
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            items(menuItems) { item ->
                when (item) {
                    is MenuRVItem.HeaderItem -> Text(
                        style = Typography.MealTitleStyle,
                        text = item.categoryName,
                        modifier = Modifier.padding(
                            start = Dimens.MarginSmall8,
                            top = Dimens.MarginSmall8
                        )
                    )

                    is MenuRVItem.SubHeaderItem -> Text(
                        text = item.categoryName,
                        style = Typography.MealTitleStyle,
                        modifier = Modifier.padding(
                            start = Dimens.MarginSmall8,
                            bottom = Dimens.MarginSmall8
                        )
                    )

                    is MenuRVItem.MealItem -> ItemMenuMeal(meal = item.meal)
                }
            }
        }
    }
}

@Composable
fun TabItem(header: MenuRVItem.HeaderItem, isSelected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.pizza),
            contentDescription = "Иконка ${header.categoryName}",
            modifier = Modifier.size(Dimens.IconSize24),
            tint = if (isSelected) Colors.Orange else Color.White
        )
        Text(
            text = header.categoryName,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = Dimens.TextSize16,
            color = if (isSelected) Colors.Orange else Color.White,
            modifier = Modifier
                .padding(horizontal = Dimens.MarginSmall8, vertical = Dimens.MarginSuperSmall4)
                .clickable { onClick() }
        )
    }
}









