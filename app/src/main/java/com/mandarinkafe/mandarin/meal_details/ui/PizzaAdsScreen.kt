package com.mandarinkafe.mandarin.meal_details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Label
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.Tag
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.FavoriteButton
import com.mandarinkafe.mandarin.menu.ui.components.tabs.SubCategoryTabItem
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Preview
@Composable
fun PizzaAdsScreenPreview() {
    val meal = Meal(
        id = "1",
        name = "Маргарита",
        description = "Томатный соус, помидоры, моцарелла, орегано и базилик",
        weight = 490,
        price = 590,
        imageUrl = "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
        isFavorite = false,
        tags = listOf(
            Tag(
                id = "1",
                name = "добавки к пицце"
            )
        ),
        labels = listOf(
            Label(
                code = "1",
                name = "Veg"
            )
        ),
        isHidden = false,
        isEditable = true
    )
    PizzaAdsScreen(meal)
}

@Composable
fun PizzaAdsScreen(meal: Meal) {
    Column(
        modifier = Modifier.background(Colors.Transparent)
    )
    {
        MealDetails(meal)
        AdsCategoryTabsRow(
            categories = TODO(),
            selectedTabIndex = TODO(),
            onTabSelected = TODO()
        )
        AddsList(
            addsItems = TODO(),
            listState = TODO(),
            modifier = TODO(),
            onEvent = TODO()
        )
    }
}

@Composable
fun MealDetails(meal: Meal) {

    Column(
        modifier = Modifier
            .padding(Dimens.MarginSmall8)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = { },
                modifier = Modifier.size(Dimens.ButtonBox32)
            ) {

                Icon(
                    modifier = Modifier.size(Dimens.ButtonToggleFavorite28),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = Colors.White
                )
            }

            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
            )

            FavoriteButton(
                meal = meal,
                onToggleFavorite = { }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = meal.imageUrl.ifEmpty { R.drawable.logo_orange_square },
                contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
                error = painterResource(R.drawable.logo_orange_square),
                placeholder = painterResource(R.drawable.logo_orange_square),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimens.MealImage136)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
                    .background(
                        color = Colors.AppBlack,
                        shape = RoundedCornerShape(Dimens.CornerRadius8)
                    )
            )
        }
        if (meal.description != null) {
            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                text = meal.description,
                style = Typography.RegularTextStyle
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (meal.weight != null && meal.weight != 0) {
                Text(
                    text = stringResource(R.string.meal_weight_template, meal.weight),
                    style = Typography.RegularTextStyle
                )
            }
            Text(
                text = stringResource(R.string.meal_price_template, meal.price),
                style = Typography.MealPriceStyle
            )
        }

    }
}

@Composable
fun AdsCategoryTabsRow(
    categories: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            containerColor = Colors.AppBlack,
            edgePadding = Dimens.ZeroDp0,
            selectedTabIndex = selectedTabIndex,
            indicator = { },
            divider = { },
        ) {
            categories.forEachIndexed { index, category ->
                SubCategoryTabItem(
                    category = category,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dimens.DividerHeight1,
            color = Colors.Grey.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun AddsList(
    addsItems: List<Meal>,
    listState: LazyListState,
    modifier: Modifier,
    onEvent: (Event) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        itemsIndexed(addsItems) { index, item ->
            AddsItem(onEvent = onEvent)

        }
    }
}

@Composable
fun AddsItem(onEvent: (Event) -> Unit) {

}