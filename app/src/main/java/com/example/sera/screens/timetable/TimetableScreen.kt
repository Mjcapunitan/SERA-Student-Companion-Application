package com.example.sera.screens.timetable

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.R
import com.example.sera.utils.onColor
import com.example.sera.utils.rememberFlowWithLifecycle
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.Schedule
import java.util.*
import kotlin.math.floor

@Composable
fun TimetableScreen(
    onBack: () -> Unit,
    onGoToSubject: (Subject) -> Unit,
) {
    TimetableScreen(
        onBack = onBack,
        onGoToSubject = onGoToSubject,
        viewModel = hiltViewModel(),
    )
}

@Composable
private fun TimetableScreen(
    onBack: () -> Unit,
    onGoToSubject: (Subject) -> Unit,
    viewModel: TimetableViewModel,
) {
    val state by rememberFlowWithLifecycle(flow = viewModel.state)
        .collectAsState(initial = TimetableState())

    TimetableScreen(
        state = state,
        onBack = onBack,
        onGoToSubject = onGoToSubject,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimetableScreen(
    state: TimetableState,
    onBack: () -> Unit,
    onGoToSubject: (Subject) -> Unit,
) {
    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        topAppBarScrollState
    )
    val days = remember { enumValues<Day>() }
    val dividerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.24f)

    val scrollState = rememberScrollState()

    val slotHeight = 72.dp

    var minOffset by remember { mutableStateOf(Dp.Infinity) }
    var doneSubjectsCount by remember { mutableIntStateOf(0) }

    val topBarColor = lerp(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        FastOutLinearInEasing.transform(if (scrollBehavior.state.overlappedFraction > 0.01f) 1f else 0f)
    )

    val slots = remember(state.subjects?.map { it.hashCode() }) {
        doneSubjectsCount = 0
        arrayListOf<TimetableSlot>().apply {
            state.subjects?.forEach { subject ->
                days.forEach { day ->
                    val schedules = subject.daySchedulesMap[day]
                    schedules?.forEach { schedule ->
                        val fromHour = schedule.from.get(Calendar.HOUR_OF_DAY)
                        val fromMinute = schedule.from.get(Calendar.MINUTE)
                        val fromTotalHour = fromHour + (fromMinute / 60f)

                        val toHour = schedule.to.get(Calendar.HOUR_OF_DAY)
                        val toMinute = schedule.to.get(Calendar.MINUTE)
                        val toTotalHour = toHour + (toMinute / 60f)

                        val offsetY = (slotHeight / 2) + (slotHeight * fromTotalHour)
                        val totalHeight = slotHeight * (toTotalHour - fromTotalHour)

                        val crossAxisOffsetFraction = day.ordinal.toFloat() / days.size
                        val crossAxisSizeFraction = 1f / days.size

                        if (offsetY < minOffset) {
                            minOffset = offsetY
                        }

                        add(
                            TimetableSlot(
                                from = schedule.from,
                                to = schedule.to,
                                subject = subject,
                                mainAxisOffset = offsetY,
                                mainAxisSize = totalHeight,
                                crossAxisOffsetFraction = crossAxisOffsetFraction,
                                crossAxisSizeFraction = crossAxisSizeFraction,
                            )
                        )
                    }
                }
                doneSubjectsCount++
            }
        }
    }

    with(LocalDensity.current) {
        LaunchedEffect(doneSubjectsCount) {
            if (doneSubjectsCount == state.subjects?.size) {
                scrollState.animateScrollTo(minOffset.toPx().toInt())
                topAppBarScrollState.contentOffset = -(minOffset.toPx())
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.timetable)) },
                modifier = Modifier
                    .background(color = topBarColor),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.CD_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth((days.size - 1).toFloat() / days.size)) {
                    (1 until days.size).forEach { _ ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .background(color = dividerColor),
                            )
                        }
                    }
                }
            }
            Column {
                Row(
                    modifier = Modifier
                        .background(color = topBarColor)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Row(modifier = Modifier.fillMaxWidth(0.9f)) {
                        days.forEach {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = it.stringValue,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(state = scrollState),
                ) {
                    val width = maxWidth * 0.9f
                    val startOffset = maxWidth * 0.1f
                    val slotShape = RoundedCornerShape(12.dp)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        (0..24).forEach { hour ->
                            Row(
                                modifier = Modifier.height(slotHeight),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(0.1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = getHourText(hour),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(color = dividerColor)
                                )
                            }
                        }
                    }
                    slots.forEach { slot ->
                        val backgroundColor = remember(slot.subject.colorValue) { slot.subject.getColor() }
                        val textColor = remember(backgroundColor) { backgroundColor.onColor() }
                        Column(
                            modifier = Modifier
                                .offset(
                                    x = (width * slot.crossAxisOffsetFraction) + startOffset,
                                    y = slot.mainAxisOffset,
                                )
                                .height(slot.mainAxisSize)
                                .width(width * slot.crossAxisSizeFraction)
                                .padding(end = 2.dp, bottom = 2.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = slotShape,
                                )
                                .clip(shape = slotShape)
                                .clickable { onGoToSubject(slot.subject) }
                                .padding(8.dp),
                        ) {
                            Text(
                                text = slot.subject.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTimetableScreen() {
    SeraTheme {
        TimetableScreen(
            state = TimetableState(
                subjects = listOf(
                    Subject(
                        title = "Subject 1",
                        daySchedulesMap = mapOf(
                            Day.MONDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.TUESDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.WEDNESDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.THURSDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.FRIDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.SATURDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                            Day.SUNDAY to
                                    listOf(Schedule(from = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 8)
                                        set(Calendar.MINUTE, 0)
                                    }, to = Calendar.getInstance().apply {
                                        set(Calendar.HOUR, 9)
                                        set(Calendar.MINUTE, 0)
                                    })),
                        ),
                        colorValue = Color.Blue.toArgb(),
                    )
                )
            ),
            onBack = {},
            onGoToSubject = {},
        )
    }
}


private fun getHourText(hour: Int): String {
    val adjustedHour = hour - (floor(hour / 24f).toInt() * 24)
    return when {
        adjustedHour == 0 -> {
            "12\nAM"
        }
        adjustedHour < 12 -> {
            "$adjustedHour\nAM"
        }
        adjustedHour == 12 -> {
            "12\nPM"
        }
        else -> {
            "${adjustedHour - 12}\nPM"
        }
    }
}
