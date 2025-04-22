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
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.MenuItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun SearchResults(
    filteredMenuItems: List<MenuItem>,
    latestSearchText: String,
    onEvent: (Event) -> Unit,
    onMealClick: () -> Unit,
) {
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
                onMealClick = {
                    onEvent(Event.SearchOnMealClick(it.id))
                    onMealClick()
                },
                onEvent = onEvent
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