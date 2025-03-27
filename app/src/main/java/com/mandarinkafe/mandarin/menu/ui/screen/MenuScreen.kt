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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.MenuRVItem
import com.mandarinkafe.mandarin.menu.ui.MenuContract
import com.mandarinkafe.mandarin.menu.ui.MenuViewModel
import com.mandarinkafe.mandarin.menu.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.menu.ui.components.MenuHeader
import com.mandarinkafe.mandarin.menu.ui.components.tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.util.ErrorScreen
import com.mandarinkafe.mandarin.util.LoadingScreen
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(viewModel: MenuViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isTopPartVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    when {
        state.isLoading -> LoadingScreen()
        state.errorMessage != null -> ErrorScreen(state.errorMessage)
        else -> MenuContentScreen(
            menuItems = state.menuItems,
            listState = listState,
            selectedTabIndex = state.selectedTabIndex,
            selectedSubTabIndex = state.selectedSubTabIndex,
            onEvent = viewModel::onEvent
        )
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is MenuContract.Effect.ShowSnackbar -> {
                    // Показываем снекбар
                }

                is MenuContract.Effect.NavigateTo -> {
                    // Навигация
                }
            }
        }
    }

    val categories = state.menuItems.filterIsInstance<MenuRVItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }

    val handleBannerClick = { targetId: String ->
        coroutineScope.launch {
            val targetIndex = state.menuItems.indexOfFirst { item ->
                when (item) {
                    is MenuRVItem.HeaderItem -> item.id == targetId
                    is MenuRVItem.SubHeaderItem -> item.id == targetId
                    is MenuRVItem.MealItem -> item.meal.id == targetId
                    else -> false
                }
            }.takeIf { it >= 0 } ?: 0
            listState.scrollToItem(targetIndex)
        }
    }

    // Отслеживание скролла для обновления активного таба
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                visibleItems.firstOrNull { it.offset >= 0 }?.index?.let { index ->
                    (state.menuItems.getOrNull(index) as? MenuRVItem.MealItem)?.let { mealItem ->
                        val parentCategory = state.menuItems
                            .takeWhile { it !== mealItem }
                            .lastOrNull { it is MenuRVItem.HeaderItem } as? MenuRVItem.HeaderItem

                        parentCategory?.let { category ->
                            val newIndex = categoriesNames.indexOf(category.categoryName)
                            if (newIndex != state.selectedTabIndex) {
                                viewModel.onEvent(MenuContract.Event.ScrollToCategory(category.id))
                            }
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
                BannerCarousel(onBannerClick = handleBannerClick)
            }
        }

        CategoryTabsRow(
            categories = categories,
            selectedTabIndex = state.selectedTabIndex,
            onTabSelected = { index ->
                viewModel.onEvent(MenuContract.Event.ScrollToCategory(categories[index].id))
                coroutineScope.launch {
                    val targetIndex = state.menuItems.indexOfFirst {
                        it is MenuRVItem.HeaderItem && it.categoryName == categories[index].categoryName
                    }
                    if (targetIndex >= 0) {
                        listState.scrollToItem(targetIndex)
                    }
                }
            }
        )

        if (state.selectedTabIndex >= 0) {
            val currentSubCategories = categories[state.selectedTabIndex].subCategoriesNames

            if (!currentSubCategories.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = currentSubCategories.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SubCategoryTabsRow(
                        categories = currentSubCategories,
                        selectedTabIndex = state.selectedSubTabIndex,
                        onTabSelected = { index ->
                            viewModel.onEvent(
                                MenuContract.Event.ScrollToCategory(
                                    currentSubCategories[index]
                                )
                            )
                            coroutineScope.launch {
                                val targetIndex = state.menuItems.indexOfFirst {
                                    it is MenuRVItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
                                }
                                if (targetIndex >= 0) {
                                    listState.scrollToItem(targetIndex)
                                }
                            }
                        }
                    )
                }
            }
        }

    }
}

