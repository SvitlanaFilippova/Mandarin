package com.mandarinkafe.mandarin.util.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.core.ui.theme.Colors

@Composable
fun ExpandableText(
    text: String,
    style: TextStyle,
    isExpanded: Boolean,
    onClick: () -> Unit,
    maxLinesCollapsed: Int
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val isTextOverflow = textLayoutResult?.hasVisualOverflow == true

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .wrapContentHeight()
    ) {
        Text(
            text = text,
            style = style,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            onTextLayout = { result ->
                textLayoutResult = result
            }
        )

        if (!isExpanded && isTextOverflow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val gradientHeight = size.height * 0.5f
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Colors.AppBlack.copy(alpha = 0.8f)
                                ),
                                startY = size.height - gradientHeight,
                                endY = size.height
                            ),
                            topLeft = Offset(0f, size.height - gradientHeight),
                            size = Size(size.width, gradientHeight)
                        )
                    }
            )

            // Иконка стрелки (отдельный слой поверх градиента)
            Box(
                modifier = Modifier
                    .matchParentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Раскрыть текст",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (8).dp),
                    tint = Colors.LightGrey
                )
            }
        }
    }
}
