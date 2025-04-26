package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleBottomSheetEffects(
    effectFlow: Flow<MenuContract.Effect>,
    onMenuEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
) {
    HandleBottomSheetEffect<MenuContract.Effect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? MenuContract.Effect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initMeal = effect.meal,
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                onMenuEvent(Event.UpdateMealFavorite(id, isFavorite))
            },
            shouldOpenCustomizationInit = effect.shouldOpenCustomization,
            onCartEvent = onCartEvent
        )
    }
}
