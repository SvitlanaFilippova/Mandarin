package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.CartMapper.toCartItem
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchResults(
    filteredMenuItems: List<MenuItem>,
    latestSearchText: String,
    onSearchEvent: (SearchContract.Event) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    onSearchDismiss: () -> Unit,
    cartState: CartContract.CartState,
    effectFlow: Flow<SearchContract.Effect>,
) {
    BackHandler {
        onSearchDismiss()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = Dimens.RadiusSearchField8,
                    bottomEnd = Dimens.RadiusSearchField8
                )
            )
            .padding(top = Dimens.ZeroDp0)
            .background(Colors.Transparent)

    ) {
        if (filteredMenuItems.isNotEmpty()) {
            SearchResultsLazyColumn(
                filteredMenuItems = filteredMenuItems,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onSearchEvent = onSearchEvent
            )
        } else if (latestSearchText.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.nothing_found),
                color = Colors.White,
                modifier = Modifier.padding(Dimens.MarginStandard16)
            )
        }
    }

    HandleBottomSheetEffect<SearchContract.Effect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? SearchContract.Effect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.meal.toCartItem(),
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                onSearchEvent(SearchContract.Event.UpdateMealFavorite(id, isFavorite))
            },
            onAddToCart = { item -> onCartEvent(CartContract.CartEvent.AddToCart(item)) }
        )
    }
}