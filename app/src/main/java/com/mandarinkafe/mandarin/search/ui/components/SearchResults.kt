package com.mandarinkafe.mandarin.search.ui.components

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
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.ui.components.HandleBottomSheetEffects
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchResults(
    filteredMenuItems: List<MenuItem>,
    latestSearchText: String,
    onMenuEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State,
    effectFlow: Flow<MenuContract.Effect>,
) {

    HandleBottomSheetEffects(
        effectFlow = effectFlow,
        onMenuEvent = onMenuEvent,
        onAddToCart = { item ->
            onCartEvent(CartContract.Event.AddToCart(item))
        }
    )


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
                onMenuEvent = onMenuEvent,
                onCartEvent = onCartEvent,
                cartState = cartState
            )
        } else if (latestSearchText.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.nothing_found),
                color = Colors.White,
                modifier = Modifier.padding(Dimens.MarginStandard16)
            )
        }
    }
}