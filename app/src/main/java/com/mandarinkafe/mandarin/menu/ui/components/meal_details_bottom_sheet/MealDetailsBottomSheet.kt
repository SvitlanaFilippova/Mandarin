package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract.Event
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    viewModel: MealDetailsViewModel = hiltViewModel(),
    onAddToCart: (CartItem) -> Unit,
    initItem: CartItem,
    shouldOpenCustomizationInit: Boolean,
    onDismiss: () -> Unit,
    onFavoriteChanged: (String, Boolean) -> Unit = { _, _ -> }
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.SetItem(initItem))
    }
    val initMeal = initItem.meal
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val state by viewModel.state.collectAsState()
    val meal = state.meal ?: initMeal

    LaunchedEffect(Unit) {
        sheetState.show()
    }
    val onClose: () -> Unit = remember(sheetState, coroutineScope) {
        {
            coroutineScope.launch {
                sheetState.hide()
                meal.let { meal ->
                    onFavoriteChanged(meal.id, meal.isFavorite)
                }
                onDismiss()
            }
        }
    }
    when {
        state.isLoading -> LoadingScreen()
        else ->

            ModalBottomSheet(
                onDismissRequest = onClose,
                sheetState = sheetState,
                containerColor = Colors.AppBlack,
                tonalElevation = Dimens.Elevation2,
                scrimColor = Colors.GreyTransparent75,

                ) {
                MealDetailsContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onAddToCart = onAddToCart,
                    onClose = onClose,
                    initItem = initItem
                )

            }
    }
}
