package com.mandarinkafe.mandarin.features.menu.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.features.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.features.menu.ui.components.MenuTopBar
import com.mandarinkafe.mandarin.features.menu.ui.components.SearchAndFilterBar
import com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.util.ui.ScrollPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    listState: LazyListState,
    onEvent: (MenuEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
    menuSate: MenuContract.MenuState,
    effectFlow: Flow<MenuContract.MenuEffect>,
) {

    val menuItems = menuSate.menuItems
    val selectedTabIndex = menuSate.selectedTabIndex
    val selectedSubTabIndex = menuSate.selectedSubTabIndex
    val selectedMenuItemIndex = menuSate.selectedMenuItemIndex

    val categories = menuItems.filterIsInstance<MenuItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    val coroutineScope = rememberCoroutineScope()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }

    val isScrollingUp = remember { mutableStateOf(false) }
    var previousPosition by remember { mutableStateOf(ScrollPosition(0, 0)) }

    val showMenuTopBar by remember {
        derivedStateOf {
            isAtTop || isScrollingUp.value
        }
    }

    val handleBannerClick = { targetName: String ->
        coroutineScope.launch {
            onEvent(
                MenuEvent.BannerClick(targetName)
            )
        }
    }

    val handleLogoClick = {
        coroutineScope.launch {
            listState.scrollToItem(index = 0)
            onEvent(MenuEvent.ScrollToTop)
        }
    }

    // Отслеживание изменения selectedMenuItemIndex и скролл при обновлении
    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex >= 0) {
            listState.scrollToItem(selectedMenuItemIndex, scrollOffset = 1)
            onEvent(MenuEvent.ResetSelectedMenuItemIndex)
        }
    }

    //  Отслеживание направления скролла
    LaunchedEffect(listState) {
        snapshotFlow {
            ScrollPosition(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset
            )
        }
            .collect { currentPosition ->
                val isScrollingUpNow = when {
                    currentPosition.index < previousPosition.index -> true
                    currentPosition.index > previousPosition.index -> false
                    else -> currentPosition.offset < previousPosition.offset
                }
                isScrollingUp.value = isScrollingUpNow
                previousPosition = currentPosition
            }
    }

    // Отслеживание скролла для обновления активного таба
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                visibleItems.firstOrNull { it.offset >= 0 }?.index?.let { index ->
                    (menuItems.getOrNull(index) as? MenuItem.MealItem)?.let { mealItem ->
                        val parentCategory = menuItems
                            .takeWhile { it !== mealItem }
                            .lastOrNull { it is MenuItem.HeaderItem } as? MenuItem.HeaderItem

                        parentCategory?.let { category ->
                            val newIndex = categoriesNames.indexOf(category.categoryName)
                            if (newIndex != selectedTabIndex) {
                                onEvent(MenuEvent.ScrollToCategory(newIndex))
                            }
                            // Ищем подкатегорию
                            val parentSubCategory = menuItems
                                .takeWhile { it !== mealItem }
                                .lastOrNull { it is MenuItem.SubHeaderItem } as? MenuItem.SubHeaderItem

                            parentSubCategory?.let { subCategory ->
                                val newSubIndex =
                                    parentCategory.subCategoriesNames?.indexOf(subCategory.categoryName)
                                        ?: -1
                                if (newSubIndex != selectedSubTabIndex) {
                                    onEvent(MenuEvent.ScrollToSubCategory(newSubIndex))
                                }
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
        // Лого-бар появляется только если пользователь в самом верху
        AnimatedVisibility(
            visible = isAtTop,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            MenuTopBar(
                onPhoneClick = { onEvent(MenuEvent.OnPhoneClick) },
                onLogoCLick = { handleLogoClick() }
            )
        }
        // Бар с поиском и фильтрами появляется всегда, когда пользователь вверху или скроллит вверх
        AnimatedVisibility(
            visible = showMenuTopBar,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SearchAndFilterBar(
                onSearchClick = { onEvent(MenuEvent.SearchOnOpenSearchClick) },
                onFilterClick = { onEvent(MenuEvent.OnLabelsClick) },
                onFavoriteClick = { onEvent(MenuEvent.OnOpenFavoritesClick) }
            )
        }
        // Баннеры только если пользователь в самом верху
        AnimatedVisibility(
            visible = isAtTop,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BannerCarousel(onBannerClick = handleBannerClick)
        }
        // Табы-категории, видно всегда
        CategoryTabsRow(
            categories = categories,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                onEvent(MenuEvent.ScrollToCategory(index))
                coroutineScope.launch {
                    val targetIndex = menuItems.indexOfFirst {
                        it is MenuItem.HeaderItem && it.categoryName == categories[index].categoryName
                    }
                    if (targetIndex >= 0) {
                        listState.scrollToItem(index = targetIndex, scrollOffset = 1)
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

                    // Табы-подкатегории, появляются при наличии в текущей категории
                    SubCategoryTabsRow(
                        categories = currentSubCategories,
                        selectedTabIndex = selectedSubTabIndex,
                        onTabSelected = { index ->
                            onEvent(MenuEvent.ScrollToSubCategory(index))
                            coroutineScope.launch {
                                val targetIndex = menuItems.indexOfFirst {
                                    it is MenuItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
                                }
                                if (targetIndex >= 0) {
                                    listState.scrollToItem(
                                        index = targetIndex,
                                        scrollOffset = 1
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        // Основное меню
        MenuList(
            menuItems = menuItems,
            listState = listState,
            modifier = Modifier.weight(1f),
            onEvent = onEvent,
            onCartEvent = onCartEvent,
            cartState = cartState,
            effectFlow = effectFlow

        )
    }
}
