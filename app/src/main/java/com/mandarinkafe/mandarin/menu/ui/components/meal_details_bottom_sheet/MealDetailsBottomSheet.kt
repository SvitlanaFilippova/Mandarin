package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads.PizzaAdsScreen
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsBottomSheet(
    meal: Meal,
    adds: List<MealCategory>,
    onEvent: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Colors.AppBlack
    ) {

        // TODO Тут добавить обработку по тегам и дальше разводить на разные экраны

        PizzaAdsScreen(
            meal = meal,
            onEvent = onEvent,
            adds = adds,
            onClose = {
                coroutineScope.launch {
                    sheetState.hide()
                    onDismiss()
                }

            })
    }
}