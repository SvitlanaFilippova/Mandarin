package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads.PizzaAdsScreen
import com.mandarinkafe.mandarin.menu.ui.screen.LoadingScreen
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract.Event
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    viewModel: MealDetailsViewModel = hiltViewModel(),
    initMeal: Meal,
    onDismiss: () -> Unit,
    onFavoriteChanged: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.SetMeal(initMeal))
    }

    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    val onClose: () -> Unit = remember(sheetState, coroutineScope) {
        {
            coroutineScope.launch {
                sheetState.hide()
                state.meal?.let { meal ->
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
                containerColor = Colors.AppBlack
            ) {

                BottomSheetHeader(
                    meal = state.meal ?: initMeal,
                    onToggleFavorite = { viewModel.onEvent(Event.ToggleFavorite) },
                    onClose = onClose
                )

                // TODO Тут добавить обработку по тегам и дальше разводить на разные экраны

                PizzaAdsScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onClose = onClose
                )
            }
    }
}