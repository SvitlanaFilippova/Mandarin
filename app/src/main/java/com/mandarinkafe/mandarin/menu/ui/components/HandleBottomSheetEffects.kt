package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleBottomSheetEffects(
    effectFlow: Flow<MenuContract.Effect>,
    onMenuEvent: (Event) -> Unit,
    onAddToCart: (CartItem) -> Unit,
) {
    HandleBottomSheetEffect<MenuContract.Effect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? MenuContract.Effect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.meal.toCartItem(),
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                onMenuEvent(Event.UpdateMealFavorite(id, isFavorite))
            },
            onAddToCart = onAddToCart
        )
    }
}
