package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
fun MealImagePlaceholder() {
    Image(
        painter = painterResource(R.drawable.placeholder_meal_no_photo),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.AppBlack),
        contentScale = ContentScale.Crop
    )
}