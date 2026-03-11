package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun CurrentTimeIndicator(
    width: Dp = 4.dp,
    offset: Dp,
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .offset(x = offset - (width / 2))
            .background(
                color = Color.White.copy(alpha = 0.72f),
                shape = RoundedCornerShape(50)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50)
            ),
    )
}