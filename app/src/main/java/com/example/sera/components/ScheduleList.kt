package com.example.sera.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Expand
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.sera.R
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.utils.Icons
import com.example.sera.common.Sera
import com.example.sera.utils.formatDate
import com.example.sera.common.value_objects.TimetableSlot
import com.example.sera.common.value_objects.entities.Subject
import java.util.*
import kotlin.math.floor

@ExperimentalMaterial3Api
@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    currentTime: Calendar = Calendar.getInstance(),
    timetableSlots: List<TimetableSlot>,
    onGoToSubject: (Subject) -> Unit,
) {
    val currentMinute = remember(currentTime) {
        currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)
    }
    val rightNow = remember(timetableSlots.hashCode(), currentMinute) {
        timetableSlots
            .filter {
                val from = it.from.get(Calendar.HOUR_OF_DAY) * 60 + it.from.get(Calendar.MINUTE)
                val to = it.to.get(Calendar.HOUR_OF_DAY) * 60 + it.to.get(Calendar.MINUTE)
                currentMinute in from until to
            }
            .sortedBy { it.from.get(Calendar.HOUR_OF_DAY) * 60 + it.from.get(Calendar.MINUTE) }
    }
    val upcoming = remember(timetableSlots.hashCode(), currentMinute) {
        timetableSlots
            .filter {
                val from = it.from.get(Calendar.HOUR_OF_DAY) * 60 + it.from.get(Calendar.MINUTE)
                currentMinute < from
            }
            .sortedBy { it.from.get(Calendar.HOUR_OF_DAY) * 60 + it.from.get(Calendar.MINUTE) }
    }
    ScheduleList(
        modifier = modifier,
        rightNow = rightNow,
        upcoming = upcoming,
        onGoToSubject = onGoToSubject,
    )
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalMaterial3Api
@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    currentTime: Calendar = Calendar.getInstance(),
    rightNow: List<TimetableSlot>,
    upcoming: List<TimetableSlot>,
    onGoToSubject: (Subject) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val upcomingEdited =
        remember(upcoming, expanded) { upcoming.take(if (expanded) Int.MAX_VALUE else 2) }

    val dots = remember(rightNow, upcomingEdited) {
        mutableStateMapOf<ScheduleListIndicatorType, Dp?>().apply {
            if (rightNow.isNotEmpty()) {
                set(ScheduleListIndicatorType.RightNowHeader, null)
                rightNow.forEach { slot ->
                    set(ScheduleListIndicatorType.RightNowItem(slot), null)
                }
            }
            if (upcomingEdited.isNotEmpty()) {
                set(ScheduleListIndicatorType.UpcomingHeader, null)
                upcomingEdited.forEach { slot ->
                    set(ScheduleListIndicatorType.UpcomingItem(slot), null)
                }
            }
        }
    }

    val areAllReady by derivedStateOf {
        var ready = true
        run loop@{
            dots.forEach { (k, v) ->
                if (v == null) {
                    ready = false
                    return@loop
                }
            }
        }
        ready
    }

    var indicatorContainerOffset by remember { mutableStateOf<Dp?>(null) }

    val density = LocalDensity.current

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .onGloballyPositioned {
                    with(density) { indicatorContainerOffset = it.positionInWindow().y.toDp() }
                },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (areAllReady && indicatorContainerOffset != null) {
                dots.entries.sortedBy { it.value }.forEachIndexed { index, mutableEntry ->
                    val dot = mutableEntry.key
                    val offset = mutableEntry.value

                    val lastOffset = if (index > 0) dots.entries.sortedBy { it.value }
                        .toList()[index - 1].value else null

                    if (offset != null) {
                        val size =
                            if (dot == ScheduleListIndicatorType.RightNowHeader || dot == ScheduleListIndicatorType.UpcomingHeader) 12.dp else 8.dp
                        val alpha =
                            if (dot == ScheduleListIndicatorType.RightNowHeader || dot is ScheduleListIndicatorType.RightNowItem) 1f else 0.38f
                        Box(
                            modifier = Modifier
                                .zIndex(0f)
                                .offset(y = lastOffset?.let {
                                    (it - (indicatorContainerOffset ?: 0.dp))
                                } ?: 0.dp)
                                .width(2.dp)
                                .height(offset - (lastOffset ?: indicatorContainerOffset ?: 0.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    shape = RoundedCornerShape(50),
                                )
                        )
                        Box(
                            modifier = Modifier
                                .zIndex(1f)
                                .offset(
                                    y = (offset - (indicatorContainerOffset ?: 0.dp)) - size / 2
                                )
                                .size(size)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = CircleShape,
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                    shape = CircleShape,
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Column(modifier = Modifier.animateContentSize()) {
                Spacer(modifier = Modifier.height(16.dp))
                if (rightNow.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.right_now),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.onGloballyPositioned {
                            with(density) {
                                dots[ScheduleListIndicatorType.RightNowHeader] =
                                    (it.positionInWindow().y + (it.size.height / 2)).toDp()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    rightNow.forEach { slot ->
                        val progress = slot.getProgress(currentTime)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onGoToSubject(slot.subject) },
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .fillMaxWidth(progress)
                                        .background(MaterialTheme.colorScheme.error)
                                )
                                BaseScheduleListItemContent(
                                    timetableSlot = slot,
                                    timeInfo = { from, _ ->
                                        Icon(
                                            imageVector = Icons.Alarm,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentTime.difference(from),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    },
                                    onIndicatorPositioned = {
                                        with(density) {
                                            dots[ScheduleListIndicatorType.RightNowItem(slot)] =
                                                (it.positionInWindow().y + (it.size.height / 2)).toDp()
                                        }
                                    },
                                )
                            }
                        }
                        if (slot != rightNow.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (upcomingEdited.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.upcoming),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.onGloballyPositioned {
                            with(density) {
                                dots[ScheduleListIndicatorType.UpcomingHeader] =
                                    (it.positionInWindow().y + (it.size.height / 2)).toDp()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    upcomingEdited.forEach { slot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onGoToSubject(slot.subject) },
                            colors = CardDefaults.outlinedCardColors(),
                            border = CardDefaults.outlinedCardBorder(),
                            elevation = CardDefaults.outlinedCardElevation(),
                        ) {
                            BaseScheduleListItemContent(
                                timetableSlot = slot,
                                timeInfo = { from, _ ->
                                    Text(
                                        text = "In ${from.difference(currentTime)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                                onIndicatorPositioned = {
                                    with(density) {
                                        dots[ScheduleListIndicatorType.UpcomingItem(slot)] =
                                            (it.positionInWindow().y + (it.size.height / 2)).toDp()
                                    }
                                },
                            )
                        }
                        if (slot != upcomingEdited.last()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            if (upcoming.size > 2) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
                    val text =
                        stringResource(id = if (expanded) R.string.see_less else R.string.see_more)
                    Icon(
                        imageVector = Icons.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.animateContentSize(),
                        text = text,
                    )
                    AnimatedVisibility(visible = !expanded) {
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (upcoming.size - 2).toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onError,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun TimetableSlot.getProgress(currentTime: Calendar): Float {
    val fromMinutes = this.from.get(Calendar.HOUR_OF_DAY) * 60 + this.from.get(Calendar.MINUTE)
    val toMinutes = this.to.get(Calendar.HOUR_OF_DAY) * 60 + this.to.get(Calendar.MINUTE)
    val currentMinutes =
        currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)

    return (currentMinutes - fromMinutes).toFloat() / (toMinutes - fromMinutes).toFloat()
}

@Composable
private fun BaseScheduleListItemContent(
    timetableSlot: TimetableSlot,
    timeInfo: @Composable (from: Calendar, to: Calendar) -> Unit,
    onIndicatorPositioned: (LayoutCoordinates) -> Unit,
) {
    val subjectColor =
        remember(timetableSlot.subject.colorValue) { timetableSlot.subject.getColor() }
    val to =
        remember(timetableSlot.to) { formatDate(Sera.timeFormat, timetableSlot.to.time) }
    val from =
        remember(timetableSlot.from) { formatDate(Sera.timeFormat, timetableSlot.from.time) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = subjectColor, shape = CircleShape)
                    .onGloballyPositioned(onGloballyPositioned = onIndicatorPositioned)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timetableSlot.subject.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            timeInfo(timetableSlot.from, timetableSlot.to)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$from - $to",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun Calendar.difference(calendar: Calendar): String {
    val thisMinutes = this.get(Calendar.HOUR_OF_DAY) * 60 + this.get(Calendar.MINUTE)
    val calendarMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

    val diffMinutes = thisMinutes - calendarMinutes

    val hour = floor(diffMinutes / 60f).toInt()
    val minutes = (diffMinutes - floor(diffMinutes / 60f) * 60).toInt()

    return if (hour == 1) {
        "1h"
    } else if (hour > 1) {
        "${hour}h"
    } else {
        "${minutes}min"
    }
}

private sealed class ScheduleListIndicatorType {
    object RightNowHeader : ScheduleListIndicatorType()
    data class RightNowItem(val slot: TimetableSlot) : ScheduleListIndicatorType()
    object UpcomingHeader : ScheduleListIndicatorType()
    data class UpcomingItem(val slot: TimetableSlot) : ScheduleListIndicatorType()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "id:pixel_6", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewScheduleList() {
    SeraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            ScheduleList(
                rightNow = listOf(
                    TimetableSlot(
                        from = Calendar.getInstance().apply {
                            set(Calendar.MINUTE, 0)
                        },
                        to = Calendar.getInstance().apply {
                            add(Calendar.MINUTE, 30)
                        },
                        subject = Subject(
                            id = 1,
                            title = "Subject 1",
                            colorValue = Color.Blue.toArgb()
                        )
                    )
                ),
                upcoming = listOf(
                    TimetableSlot(
                        from = Calendar.getInstance().apply {
                            add(Calendar.HOUR_OF_DAY, 1)
                            set(Calendar.MINUTE, 0)
                        },
                        to = Calendar.getInstance().apply {
                            add(Calendar.HOUR_OF_DAY, 2)
                            set(Calendar.MINUTE, 0)
                        },
                        subject = Subject(
                            id = 2,
                            title = "Subject 2",
                            colorValue = Color.Green.toArgb()
                        )
                    ),
                    TimetableSlot(
                        from = Calendar.getInstance().apply {
                            add(Calendar.HOUR_OF_DAY, 3)
                            set(Calendar.MINUTE, 0)
                        },
                        to = Calendar.getInstance().apply {
                            add(Calendar.HOUR_OF_DAY, 4)
                            set(Calendar.MINUTE, 0)
                        },
                        subject = Subject(
                            id = 3,
                            title = "Subject 3",
                            colorValue = Color.Yellow.toArgb()
                        )
                    )
                ),
                onGoToSubject = {},
            )
        }
    }
}
