package com.example.sera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.common.value_objects.schedule.DayScheduleMap
import com.example.sera.common.value_objects.schedule.RecurringSchedule
import com.example.sera.common.value_objects.schedule.Schedule
import com.example.sera.common.value_objects.schedule.getDaySchedules
import java.util.*

@Composable
fun ScheduleView(modifier: Modifier = Modifier, schedules: DayScheduleMap) {
    Column(modifier = modifier) {
        var nextIndex = 0
        schedules.entries.forEachIndexed { index, entry ->
            if (index != nextIndex) return@forEachIndexed

            if (entry.value.isEmpty()) {
                EmptyDaySchedule(day = entry.key)
            } else {
                val daysToShow = mutableListOf(entry.key)
                kotlin.run loop@{
                    (index + 1 until schedules.size).forEach {
                        val otherKey = schedules.keys.toList()[it]
                        if (entry.value == schedules[otherKey]) {
                            daysToShow.add(otherKey)
                            nextIndex++
                        } else {
                            return@loop
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                if (daysToShow.size == 1) {
                    SingleDaySchedule(day = entry.key, schedules = entry.value)
                } else {
                    DayRangeSchedule(
                        fromDay = entry.key,
                        toDay = daysToShow.last(),
                        schedules = entry.value
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            nextIndex++
        }
    }
}

@Composable
fun EmptyDaySchedule(day: Day) {
    Row(
        modifier = Modifier.padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = day.name.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SingleDaySchedule(day: Day, schedules: List<Schedule>) {
    ConstraintLayout {
        val (dayBox, line, dayText, schedulesContainer) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(dayBox) {}
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.stringValue,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .constrainAs(line) {
                    top.linkTo(dayBox.bottom, margin = 8.dp)
                    start.linkTo(dayBox.start)
                    end.linkTo(dayBox.end)
                    bottom.linkTo(schedulesContainer.bottom)
                    height = Dimension.fillToConstraints
                }
                .width(2.dp)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                .fillMaxHeight()
        )
        Text(
            modifier = Modifier.constrainAs(dayText) {
                top.linkTo(dayBox.top)
                bottom.linkTo(dayBox.bottom)
                start.linkTo(dayBox.end, margin = 16.dp)
            },
            text = day.name.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        DayScheduleText(
            modifier = Modifier
                .constrainAs(schedulesContainer) {
                    top.linkTo(dayText.bottom, margin = 4.dp)
                }
                .padding(start = 56.dp, bottom = 8.dp),
            schedules = schedules
        )
    }
}

@Composable
fun DayRangeSchedule(fromDay: Day, toDay: Day, schedules: List<Schedule>) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(0.dp, 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fromDay.stringValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .padding(0.dp, 2.dp)
                        .height(8.dp)
                        .width(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
                Text(
                    text = toDay.stringValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "${
                    fromDay.name.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                } - ${
                    toDay.name.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            DayScheduleText(schedules = schedules)
        }
    }
    Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
        )
    }
}

@Composable
fun DayScheduleText(modifier: Modifier = Modifier, schedules: List<Schedule>) {
    Column(modifier = modifier) {
        schedules.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = it.getScheduleString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(text = it.room ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyScheduleView() {
    SeraTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            ScheduleView(
                schedules = listOf<RecurringSchedule>().getDaySchedules()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSingleDayScheduleView() {
    SeraTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            ScheduleView(
                schedules = listOf(
                    RecurringSchedule(
                        schedule = Schedule(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 7)
                                set(Calendar.MINUTE, 0)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 0)
                            }
                        ),
                        days = Days(set = mutableSetOf(Day.TUESDAY))
                    )
                ).getDaySchedules()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDayRangeScheduleView() {
    SeraTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            ScheduleView(
                schedules = listOf(
                    RecurringSchedule(
                        schedule = Schedule(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 7)
                                set(Calendar.MINUTE, 0)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 0)
                            }
                        ),
                        days = Days(set = mutableSetOf(Day.TUESDAY, Day.WEDNESDAY))
                    )
                ).getDaySchedules()
            )
        }
    }
}