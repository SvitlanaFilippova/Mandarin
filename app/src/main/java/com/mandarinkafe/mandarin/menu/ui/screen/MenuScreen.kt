package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
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

    val categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Следим за первым видимым элементом в списке блюд
    LaunchedEffect(listState.firstVisibleItemIndex) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val item = menuItems.getOrNull(index)
                if (item is MenuRVItem.HeaderItem) {
                    val newIndex = categoriesNames.indexOf(item.categoryName)
                    if (newIndex != selectedTabIndex) {
                        selectedTabIndex = newIndex
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {

        CategoryTabs(
            categories = categories,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
                coroutineScope.launch {
                    val targetIndex = menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == categories[index].categoryName
                    }
                    if (targetIndex >= 0) {
                        listState.scrollToItem(targetIndex)
                    }
                }
            }
        )

        MenuList(menuItems, listState, modifier = Modifier.weight(1f))
    }
}

@Composable
fun CategoryTabs(
    categories: List<MenuRVItem.HeaderItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        containerColor = Colors.AppBlack,
        selectedTabIndex = selectedTabIndex,
        edgePadding = Dimens.MarginSmall8,
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Colors.Orange
            )
        }
    ) {
        categories.forEachIndexed { index, category ->
            CategoryTab(
                category = category,
                isSelected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
fun CategoryTab(category: MenuRVItem.HeaderItem, isSelected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                category.categoryName,
                color = if (isSelected) Colors.Orange else Color.White
            )
        },
        icon = {
            AsyncImage(
                model = category.tabIcon,
                contentDescription = "Иконка ${category.categoryName}",
                modifier = Modifier.size(Dimens.IconSize24),
                error = painterResource(R.drawable.logo_orange),
                placeholder = painterResource(R.drawable.logo_orange),
                colorFilter = ColorFilter.tint(if (isSelected) Colors.Orange else Color.White)
            )
        },
        selectedContentColor = Colors.Orange,
    )
}

@Composable
fun MenuList(menuItems: List<RVItem>, listState: LazyListState, modifier: Modifier ) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        items(menuItems) { item ->
            when (item) {
                is MenuRVItem.HeaderItem -> Text(
                    text = item.categoryName,
                    style = Typography.MealTitleStyle,
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

