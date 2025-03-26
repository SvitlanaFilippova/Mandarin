package com.mandarinkafe.mandarin.menu.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.domain.models.mockMenuData
import com.mandarinkafe.mandarin.menu.ui.components.BannerCarouselPreview
import com.mandarinkafe.mandarin.menu.ui.components.MenuHeader
import com.mandarinkafe.mandarin.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.util.RVItem
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

    // Верхняя часть экрана видна только если пользователь в самом верху списка
    val isTopPartVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    val categories = menuItems.filterIsInstance<MenuRVItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    var selectedTabIndex by remember { mutableIntStateOf(-1) }
    var isUserScrolled by remember { mutableStateOf(false) } // Флаг, был ли скролл


    LaunchedEffect(listState.firstVisibleItemIndex) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (!isUserScrolled && index > 0) {
                    isUserScrolled = true // Пользователь начал скроллить
                }
                if (isUserScrolled) {
                    val item = menuItems.getOrNull(index)
                    if (item is MenuRVItem.HeaderItem) {
                        val newIndex = categoriesNames.indexOf(item.categoryName)
                        if (newIndex != selectedTabIndex) {
                            selectedTabIndex = newIndex
                        }
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {

        AnimatedVisibility(
            visible = isTopPartVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MenuHeader()
                BannerCarouselPreview()
            }
        }

        CategoryTabsRow(
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
        if (selectedTabIndex >= 0) {
            val currentSubCategories = categories[selectedTabIndex].subCategoriesNames

            if (!currentSubCategories.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = currentSubCategories.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SubCategoryTabsRow(
                        categories = currentSubCategories,
                        selectedTabIndex = 0
                    ) { }
                }
            }
        }
        MenuList(menuItems, listState, modifier = Modifier.weight(1f))
    }
}