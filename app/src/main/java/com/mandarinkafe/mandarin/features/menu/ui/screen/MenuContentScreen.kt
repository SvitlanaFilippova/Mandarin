package com.mandarinkafe.mandarin.features.menu.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.ui.components.BackToTopFAB
import com.mandarinkafe.mandarin.features.menu.ui.components.BannerCarousel
import com.mandarinkafe.mandarin.features.menu.ui.components.MenuList
import com.mandarinkafe.mandarin.features.menu.ui.components.SearchBar
import com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs.CategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.ui.components.category_tabs.SubCategoryTabsRow
import com.mandarinkafe.mandarin.features.menu.ui.components.getVisibleCategoryIndexes
import com.mandarinkafe.mandarin.features.menu.ui.models.MenuItem
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.shared.ui.view_model.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.util.Constants.FORCE_SHOW_FAB_DURATION_MS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MenuContentScreen(
    listState: LazyListState,
    onEvent: (MenuEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    cartState: CartContract.CartState,
    menuSate: MenuContract.MenuState,
) {

    val menuItems = menuSate.menuItems
    val selectedTabIndex = menuSate.selectedTabIndex
    val selectedSubTabIndex = menuSate.selectedSubTabIndex
    val selectedMenuItemIndex = menuSate.selectedMenuItemIndex

    val categories = menuItems.filterIsInstance<MenuItem.HeaderItem>()
    val categoriesNames = categories.map { it.categoryName }
    val coroutineScope = rememberCoroutineScope()

    val isScrollingUp = remember { mutableStateOf(false) }
    val isScrollingDown = remember { mutableStateOf(false) }

    var previousIndex by remember { mutableIntStateOf(0) }
    var previousOffset by remember { mutableIntStateOf(0) }

    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset == 0
        }
    }

    val showMenuTopBar by remember {
        derivedStateOf {
            isAtTop || isScrollingUp.value
        }
    }

    val forceShowBackToTopFAB = remember { mutableStateOf(false) }
    val showBackToTopFAB by remember {
        derivedStateOf {
            forceShowBackToTopFAB.value || (!isAtTop && !isScrollingDown.value)
        }
    }

    val handleBannerClick = { banner: Banner ->
        coroutineScope.launch {
            onEvent(MenuEvent.BannerClick(banner))
            forceShowBackToTopFAB.value = true
            delay(FORCE_SHOW_FAB_DURATION_MS)
            forceShowBackToTopFAB.value = false
        }
    }

    val handleBackToTopClick = {
        coroutineScope.launch {
            listState.scrollToItem(index = 0)
            onEvent(MenuEvent.ScrollToTop)
        }
    }
    // Скрыть/показать TopBar в зависимости от видимой части экрана
    LaunchedEffect(isAtTop) {
        if (isAtTop) {
            onSharedEvent(SharedEvent.ShowTopBar)
        } else {
            onSharedEvent(SharedEvent.HideTopBar)
        }
    }

    // Отслеживание изменения selectedMenuItemIndex и скролл при обновлении
    LaunchedEffect(selectedMenuItemIndex) {
        if (selectedMenuItemIndex >= 0) {
            listState.scrollToItem(selectedMenuItemIndex, scrollOffset = 1)
            onEvent(MenuEvent.ResetSelectedMenuItemIndex)
        }
    }

    // Отслеживание направления скролла
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val deltaIndex = index - previousIndex
            val deltaOffset = offset - previousOffset

            val isScrollingDownNow = when {
                deltaIndex > 0 -> true
                deltaIndex < 0 -> false
                else -> deltaOffset > 0
            }

            val isScrollingUpNow = when {
                deltaIndex < 0 -> true
                deltaIndex > 0 -> false
                else -> deltaOffset < 0
            }

            isScrollingDown.value = isScrollingDownNow
            isScrollingUp.value = isScrollingUpNow

            previousIndex = index
            previousOffset = offset
        }
    }

    // Отслеживание скролла для обновления активного таба
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val (newCategoryIndex, newSubIndex) = getVisibleCategoryIndexes(
                    visibleItems = visibleItems,
                    menuItems = menuItems,
                    categoriesNames = categoriesNames
                )

                if (newCategoryIndex != null && newCategoryIndex != selectedTabIndex) {
                    onEvent(MenuEvent.ScrollToCategory(newCategoryIndex))
                }
                if (newSubIndex != null && newSubIndex != selectedSubTabIndex) {
                    onEvent(MenuEvent.ScrollToSubCategory(newSubIndex))
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Бар с поиском и фильтрами появляется всегда, когда пользователь вверху или скроллит вверх
            AnimatedVisibility(
                visible = showMenuTopBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SearchBar(
                    onSearchClick = { onEvent(MenuEvent.SearchOnOpenSearchClick) },
                )
            }

            // Баннеры видны только если пользователь в самом верху
            AnimatedVisibility(
                visible = isAtTop,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                if (menuSate.bannersAreLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.MarginStandard16)
                            .aspectRatio(2.91f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Colors.LightGrey,
                            strokeWidth = Dimens.ProgressBarSmallWidth8,
                            trackColor = Colors.DarkGrey
                        )
                    }

                } else
                    if (!menuSate.banners.isEmpty()) {
                        BannerCarousel(
                            banners = menuSate.banners,
                            onBannerClick = handleBannerClick
                        )
                    }
            }

            // Табы-категории видны всегда
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

                // Табы-подкатегории, появляются при наличии в текущей категории
                if (!currentSubCategories.isNullOrEmpty()) {
                    AnimatedVisibility(
                        visible = currentSubCategories.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
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
                modifier = Modifier
                    .weight(1f),
                onEvent = onEvent,
                onCartEvent = onCartEvent,
                cartState = cartState,
            )
        }

        // FAB для возврата наверх, видна когда юзер не скролит вниз и не находится наверху экрана
        AnimatedVisibility(
            visible = showBackToTopFAB,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Dimens.MarginStandard16)
        ) {
            BackToTopFAB(
                onClick = { handleBackToTopClick() }
            )
        }
    }
}
