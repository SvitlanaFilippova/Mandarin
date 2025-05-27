package com.mandarinkafe.mandarin.features.menu.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.domain.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal

import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleBottomSheetEffects(
    effectFlow: Flow<MenuContract.MenuEffect>,
    onMenuEvent: (MenuEvent) -> Unit,
    onAddToCart: (CustomizedMeal) -> Unit,
) {
    HandleBottomSheetEffect<MenuContract.MenuEffect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? MenuContract.MenuEffect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.meal.toCustomizedMeal(),
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                onMenuEvent(MenuEvent.UpdateMealFavorite(id, isFavorite))
            },
            onAddToCart = onAddToCart
        )
    }
}
