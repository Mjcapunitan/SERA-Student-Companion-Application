package com.example.sera.common.compose_ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.sera.common.value_objects.entities.Subject

@Composable
internal fun Slot(
    subject: Subject,
    offset: DpOffset,
    width: Dp,
    slotHeight: Dp,
    subjectLabelHeight: Dp,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
) {
    with(LocalDensity.current) {
        val textColor by animateColorAsState(targetValue = rememberSlotTextColor(selected))
        val slotColor by animateColorAsState(targetValue = rememberSlotColor(selected, subject))
        Column(
            modifier = Modifier
                .width(width)
                .offset(
                    x = offset.x,
                    y = offset.y,
                )
        ) {
            Text(
                text = subject.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = subjectLabelHeight.toSp(),
                    color = textColor
                ),
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier
                    .height(slotHeight)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = slotColor,
                    )
                    .clickable { onClick?.invoke() },
            )
        }
    }
}

@Composable
private fun rememberSlotTextColor(selected: Boolean): Color {
    return MaterialTheme.colorScheme.onBackground.copy(
        alpha = if (selected) 1f else 0.38f
    )
}

@Composable
private fun rememberSlotColor(selected: Boolean, subject: Subject): Color {
    val subjectColor = remember(subject) { subject.getColor() }
    return subjectColor.copy(
        alpha = if (selected) 1f else 0.38f
    )
}