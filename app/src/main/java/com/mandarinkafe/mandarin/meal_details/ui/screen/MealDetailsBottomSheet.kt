package com.mandarinkafe.mandarin.meal_details.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.meal_details.ui.components.MealDetailsContent
import com.mandarinkafe.mandarin.meal_details.ui.view_model.MealDetailsContract.Event
import com.mandarinkafe.mandarin.meal_details.ui.view_model.MealDetailsViewModel
import com.mandarinkafe.mandarin.util.ui.components.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    viewModel: MealDetailsViewModel = hiltViewModel(),
    onAddToCart: (CartItem) -> Unit,
    initItem: CartItem,
    onDismiss: () -> Unit,
    onFavoriteChanged: (String, Boolean) -> Unit = { _, _ -> }
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.SetItem(initItem))
    }
    val state by viewModel.state.collectAsState()
    val initMeal = initItem.meal
    val meal = state.customizedMeal?.meal ?: initMeal
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.BSMarginForStatusBar40),
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
