package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography.PlaceholderTitleStyle
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.meal_details.ui.screen.MealDetailsBottomSheet
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract
import com.mandarinkafe.mandarin.util.ui.HandleBottomSheetEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchResults(
    filteredMenuItems: List<Meal>,
    latestSearchText: String,
    onSearchEvent: (SearchContract.SearchEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    onSearchDismiss: () -> Unit,
    cartState: CartContract.CartState,
    effectFlow: Flow<SearchContract.SearchEffect>,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.Transparent),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(R.drawable.placeholder_nothing_found),
                    contentDescription = stringResource(id = R.string.nothing_found),
                    modifier = Modifier
                        .width(Dimens.PlaceholderImageSize200)
                        .padding(vertical = Dimens.MarginStandard16)
                )
                Text(
                    text = stringResource(id = R.string.nothing_found),
                    color = Colors.White,
                    modifier = Modifier.padding(Dimens.MarginBig24),
                    style = PlaceholderTitleStyle
                )
            }
        }
    }

    HandleBottomSheetEffect<SearchContract.SearchEffect.OpenMealDetailsBS>(
        effectFlow = effectFlow,
        cast = { it as? SearchContract.SearchEffect.OpenMealDetailsBS }
    ) { effect, onDismiss ->
        MealDetailsBottomSheet(
            initItem = effect.meal.toCustomizedMeal(),
            onDismiss = onDismiss,
            onFavoriteChanged = { id, isFavorite ->
                onSearchEvent(SearchContract.SearchEvent.UpdateMealFavorite(id, isFavorite))
            },
            onAddToCart = { item -> onCartEvent(CartContract.CartEvent.AddToCart(item)) }
        )
    }
}