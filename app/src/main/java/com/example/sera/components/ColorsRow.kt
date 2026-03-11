package com.example.sera.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sera.ui.theme.SeraTheme
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.example.sera.R

@Composable
fun ColorsRow(
    selectedColor: Color,
    onSetSelectedColor: (Color) -> Unit,
    onSetShowColorPickerDialog: (Boolean) -> Unit,
) {
    FlowRow(
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = FlowMainAxisAlignment.Start,
    ) {
        val isCustom by with(SeraTheme.colors) {
            derivedStateOf { selectedColor !in subjectColors }
        }
        SeraTheme.colors.subjectColors.forEach { color ->
            val borderWidth by animateDpAsState(targetValue = if (color == selectedColor) 4.dp else 0.dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .background(color = color, shape = CircleShape)
                    .border(
                        width = borderWidth,
                        color = Color(0xFFE6E6E6),
                        shape = CircleShape,
                    )
                    .clip(shape = CircleShape)
                    .clickable { onSetSelectedColor(color) }
            )
        }
        val borderWidth by animateDpAsState(targetValue = if (isCustom) 4.dp else 0.dp)
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .background(
                    color = if (isCustom) selectedColor else Color(0xFFEFEFEF),
                    shape = CircleShape
                )
                .border(
                    width = borderWidth,
                    color = Color(0xFFE6E6E6),
                    shape = CircleShape,
                )
                .clip(shape = CircleShape)
                .clickable { onSetShowColorPickerDialog(true) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.palette),
                contentDescription = null,
                tint = if (isCustom) contentColorFor(backgroundColor = selectedColor) else MaterialTheme.colors.primary,
            )
        }
    }
}