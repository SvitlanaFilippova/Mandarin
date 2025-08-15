package com.mandarinkafe.mandarin.features.menu.presentation.ui.screen

//
//@Composable
//internal fun createMenuScrollUi(
//    listState: LazyListState,
//    selectedTabIndex: Int,
//    selectedSubTabIndex: Int,
//    selectedMenuItemIndex: Int,
//    menuItems: List<MenuItem>,
//    categoriesNames: List<String>,
//    onMenuEvent: (MenuEvent) -> Unit,
//    onSharedEvent: (SharedEvent) -> Unit,
//): ScrollUi {
//    Log.d("DEBUG Scroll", "createMenuScrollUi created")
//    var isScrollingUp by remember { mutableStateOf(false) }
//    var isScrollingDown by remember { mutableStateOf(false) }
//    var previousIndex by remember { mutableIntStateOf(0) }
//    var previousOffset by remember { mutableIntStateOf(0) }
//
//    val isAtTop by remember {
//        derivedStateOf {
//            listState.firstVisibleItemScrollOffset == 0
//        }
//    }
//
//    val coroutineScope = rememberCoroutineScope()
//
//    val showMenuTopBar by remember {
//        derivedStateOf { isAtTop || isScrollingUp }
//    }
//
//    var forceShowBackToTopFAB by remember { mutableStateOf(false) }
//    val showBackToTopFAB by remember {
//        derivedStateOf { forceShowBackToTopFAB || !isAtTop && !isScrollingDown }
//    }
//
//    SetupTopBarVisibility(isAtTop, onSharedEvent)
//    SetupAutoScrollToItem(selectedMenuItemIndex, listState, onMenuEvent)
//    SetupTabTracking(
//        listState,
//        menuItems,
//        categoriesNames,
//        selectedTabIndex,
//        selectedSubTabIndex,
//        onMenuEvent
//    )
//
//    LaunchedEffect(listState) {
//        trackScrollDirection(
//            listState = listState,
//            previousIndex = { previousIndex },
//            previousOffset = { previousOffset },
//            updatePrevious = { index, offset ->
//                previousIndex = index
//                previousOffset = offset
//            },
//            onDirectionChanged = { dir ->
//                isScrollingUp = dir == ScrollDirection.UP
//                isScrollingDown = dir == ScrollDirection.DOWN
//            }
//        )
//    }
//
//    fun onBannerClick(banner: Banner) {
//        coroutineScope.launch {
//            onMenuEvent(MenuEvent.BannerClick(banner))
//            forceShowBackToTopFAB = true
//            delay(FORCE_SHOW_FAB_DURATION_MS)
//            forceShowBackToTopFAB = false
//        }
//    }
//
//    fun onBackToTopClick() {
//        coroutineScope.launch {
//            listState.scrollToItem(index = 0)
//            onMenuEvent(MenuEvent.ScrollToTop)
//        }
//    }
//
//    fun scrollToCategory(index: Int) {
//        onMenuEvent(MenuEvent.ScrollToCategory(index))
//        coroutineScope.launch {
//            val targetIndex = menuItems.indexOfFirst {
//                it is MenuItem.HeaderItem && it.categoryName == categoriesNames[index]
//            }
//            if (targetIndex >= 0) {
//                listState.scrollToItem(index = targetIndex, scrollOffset = 1)
//            }
//        }
//    }
//
//    fun scrollToSubCategory(index: Int, currentSubCategories: List<String>) {
//        onMenuEvent(MenuEvent.ScrollToSubCategory(index))
//        coroutineScope.launch {
//            val targetIndex = menuItems.indexOfFirst {
//                it is MenuItem.SubHeaderItem && it.categoryName == currentSubCategories[index]
//            }
//            if (targetIndex >= 0) {
//                listState.scrollToItem(
//                    index = targetIndex,
//                    scrollOffset = 1
//                )
//            }
//        }
//    }
//
//    return ScrollUi(
//        isAtTop = isAtTop,
//        showMenuTopBar = showMenuTopBar,
//        showBackToTopFAB = showBackToTopFAB,
//        onBannerClick = ::onBannerClick,
//        onBackToTopClick = ::onBackToTopClick,
//        scrollToCategory = ::scrollToCategory,
//        scrollToSubCategory = ::scrollToSubCategory
//    )
//}
//
//@Composable
//private fun SetupTopBarVisibility(
//    isAtTop: Boolean,
//    onSharedEvent: (SharedEvent) -> Unit
//) {
//    LaunchedEffect(isAtTop) {
//        if (isAtTop) {
//            onSharedEvent(SharedEvent.ShowTopBar)
//        } else {
//            onSharedEvent(SharedEvent.HideTopBar)
//        }
//    }
//}
//
//@Composable
//private fun SetupAutoScrollToItem(
//    selectedIndex: Int,
//    listState: LazyListState,
//    onMenuEvent: (MenuEvent) -> Unit
//) {
//    LaunchedEffect(selectedIndex) {
//        if (selectedIndex >= 0) {
//            listState.scrollToItem(selectedIndex, scrollOffset = 1)
//            onMenuEvent(MenuEvent.ResetSelectedMenuItemIndex)
//        }
//    }
//}
//
//@Composable
//private fun SetupTabTracking(
//    listState: LazyListState,
//    menuItems: List<MenuItem>,
//    categoriesNames: List<String>,
//    selectedTabIndex: Int,
//    selectedSubTabIndex: Int,
//    onMenuEvent: (MenuEvent) -> Unit
//) {
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
//            .collect { visibleItems ->
//                val (newCategoryIndex, newSubIndex) = getVisibleCategoryIndexes(
//                    visibleItems = visibleItems,
//                    menuItems = menuItems,
//                    categoriesNames = categoriesNames
//                )
//
//                if (newCategoryIndex != null && newCategoryIndex != selectedTabIndex) {
//                    onMenuEvent(MenuEvent.ScrollToCategory(newCategoryIndex))
//                }
//                if (newSubIndex != null && newSubIndex != selectedSubTabIndex) {
//                    onMenuEvent(MenuEvent.ScrollToSubCategory(newSubIndex))
//                }
//            }
//    }
//}
//
//
//
//
