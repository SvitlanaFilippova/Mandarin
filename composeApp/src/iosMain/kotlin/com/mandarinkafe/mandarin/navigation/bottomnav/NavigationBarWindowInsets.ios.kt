package com.mandarinkafe.mandarin.navigation.bottomnav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/** Part of the system bottom inset: 1.0 matches full [WindowInsets.navigationBars] padding. */
private const val BottomInsetFraction = 0.4f

private class FractionalBottomInsets(
    private val source: WindowInsets,
    private val fraction: Float,
) : WindowInsets {
    override fun getLeft(density: Density, layoutDirection: LayoutDirection) = 0
    override fun getTop(density: Density) = 0
    override fun getRight(density: Density, layoutDirection: LayoutDirection) = 0
    override fun getBottom(density: Density): Int =
        (source.getBottom(density) * fraction).toInt()
}

/**
 * Full Material3 bottom insets read as oversized on iOS; zero felt flush with the home area.
 * Horizontal [systemBars] + a fraction of [navigationBars] bottom is a workable middle ground.
 */
@Composable
actual fun navigationBarWindowInsets(): WindowInsets {
    val navigationBars = WindowInsets.navigationBars
    val horizontal = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    val fractionalBottom = remember(navigationBars) {
        FractionalBottomInsets(navigationBars, BottomInsetFraction)
    }
    return horizontal.union(fractionalBottom)
}
