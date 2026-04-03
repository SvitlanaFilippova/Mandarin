package com.mandarinkafe.mandarin.navigation.bottomnav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable

/** Platform-specific insets for [androidx.compose.material3.NavigationBar]. */
@Composable
expect fun navigationBarWindowInsets(): WindowInsets
