package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.EditableType
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
    shouldOpenCustomizationInit: Boolean,
    onDismiss: () -> Unit,
    onFavoriteChanged: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.SetMeal(initMeal))
    }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var shouldOpenCustomization by remember { mutableStateOf(shouldOpenCustomizationInit) }

    val state by viewModel.state.collectAsState()
    val meal = state.meal ?: initMeal

    val totalPrice = meal.price + meal.adds.sumOf { it.price }

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
                containerColor = Colors.AppBlack,
                tonalElevation = Dimens.Elevation2,
                scrimColor = Colors.GreyTransparent75,

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.MarginSmall8)
                ) {
                    BottomSheetHeader(
                        meal = meal,
                        onToggleFavorite = { viewModel.onEvent(Event.ToggleFavorite) },
                        onClose = onClose
                    )
                    if (!shouldOpenCustomization) {
                        MealImage(
                            meal = meal
                        )
                    }

                    MealInfo(
                        meal = meal
                    )


                    if (shouldOpenCustomization) {
                        when (meal.editableType) {
                            EditableType.PIZZA -> PizzaAdsScreen(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onClose = onClose
                            )

                            EditableType.MODIFIABLE -> {}
                            EditableType.WOK -> {}
                            null -> {}
                        }

                    }


                if (!shouldOpenCustomization && meal.editableType != null) {
                    OpenCustomizationButton(
                        modifier = Modifier.padding(Dimens.MarginSmall8),
                        editableType = meal.editableType,
                        onClick = { shouldOpenCustomization = true }
                    )
                }

                ToCartButton(
                    modifier = Modifier.padding(Dimens.MarginSmall8),
                    totalPrice = totalPrice,
                    onClick = {
                        viewModel.onEvent(Event.AddToCart)
                        onClose()
                    }
                )
            }
            }
    }
}