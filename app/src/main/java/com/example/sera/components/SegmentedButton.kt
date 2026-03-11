package com.example.sera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.utils.Icons
import com.example.sera.common.value_objects.ScheduleViewButtonItem
import com.example.sera.common.value_objects.SegmentedButtonItem

/**
 * Own implementation of Material 3 Segmented Button.
 *
 * TODO: Replace with official implementation from Material 3 compose library
 */
@Composable
fun <D, T : SegmentedButtonItem<D>> SegmentedButton(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: T,
    onSelectItem: (T) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .border(
                width = 1.dp, color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(50),
            )
    ) {
        items.forEach {
            val isSelected = selected == it
            val startCornerPercent = if (items.first() == it) 50 else 0
            val endCornerPercent = if (items.last() == it) 50 else 0
            val shape = RoundedCornerShape(
                topStartPercent = startCornerPercent,
                bottomStartPercent = startCornerPercent,
                topEndPercent = endCornerPercent,
                bottomEndPercent = endCornerPercent,
            )
            Row(
                modifier = Modifier
                    .clip(shape)
                    .clickable { onSelectItem(it) }
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        shape = shape,
                    )
                    .padding(12.dp, 0.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(visible = isSelected) {
                    Row {
                        Icon(
                            imageVector = Icons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Text(
                    text = stringResource(id = it.text),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (it != items.last()) {
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(color = MaterialTheme.colorScheme.outline))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSegmentedButton() {
    SeraTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            SegmentedButton(
                items = listOf(
                    ScheduleViewButtonItem.List,
                    ScheduleViewButtonItem.Timetable,
                ),
                selected = ScheduleViewButtonItem.List,
                onSelectItem = {},
            )
        }
    }
}