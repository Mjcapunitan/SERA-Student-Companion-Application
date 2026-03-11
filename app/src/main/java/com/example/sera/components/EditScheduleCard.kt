package com.example.sera.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sera.R
import com.example.sera.components.ClickableDayItem
import com.example.sera.utils.Icons
import com.example.sera.utils.showTimePicker
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.schedule.Schedule
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleCard(
    time: String,
    onSetSchedule: (Schedule) -> Unit,
    days: Set<Day>,
    daysToDisable: Set<Day>,
    room: String?,
    onUpdateRoom: (String) -> Unit,
    onDayClick: (Day) -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.outlinedCardElevation(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val context = LocalContext.current
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            var from: Calendar
                            var to: Calendar
                            showTimePicker(
                                context = context,
                                title = R.string.from,
                                onTimeSet = { setFrom ->
                                    from = setFrom.apply {
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    showTimePicker(
                                        context = context,
                                        title = R.string.to,
                                        hourOfDay = (setFrom.get(Calendar.HOUR_OF_DAY) + 1).coerceAtMost(
                                            23
                                        ),
                                        minute = setFrom.get(Calendar.MINUTE),
                                        onTimeSet = { setTo ->
                                            to = setTo.apply {
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            if (to.after(from)) {
                                                onSetSchedule(Schedule(from, to))
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Invalid time",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        },
                                        onCancel = {})
                                },
                                onCancel = {},
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                        mainAxisAlignment = FlowMainAxisAlignment.Start,
                    ) {
                        enumValues<Day>().forEach {
                            ClickableDayItem(
                                day = it.stringValue,
                                isSelected = days.contains(it),
                                isDisabled = daysToDisable.contains(it),
                                onClick = { onDayClick(it) })
                        }
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Close,
                        contentDescription = stringResource(R.string.CD_delete_schedule),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = room ?: "",
                onValueChange = onUpdateRoom,
                label = {
                    Text(text = stringResource(id = R.string.room))
                },
            )
        }
    }
}