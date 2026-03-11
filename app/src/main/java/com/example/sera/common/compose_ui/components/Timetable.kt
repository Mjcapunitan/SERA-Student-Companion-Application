package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.common.value_objects.TimetableSlot
import com.example.sera.common.value_objects.entities.Subject
import java.util.*


@Composable
fun Timetable(
    state: TimetableState,
    list: List<TimetableSlot>,
    onSubjectClick: (Subject) -> Unit,
    slotHeight: Dp = 24.dp,
    subjectLabelHeight: Dp = 10.dp,
) {
    val singleHourWidth by state.singleHourWidth.collectAsState()
    var boxHeight by remember { mutableStateOf(0.dp) }

    val listOffsets = remember(list, singleHourWidth, slotHeight, subjectLabelHeight) {
        getListOffsets(
            list = list,
            singleHourWidth = singleHourWidth,
            slotHeight = slotHeight,
            subjectLabelHeight = subjectLabelHeight,
            onUpdateBoxHeight = { boxHeight = it },
        )
    }

    Box(
        modifier = Modifier
            .horizontalScroll(state = state.scrollState)
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
    ) {
        var timeRowHeight by remember { mutableStateOf(0) }
        TimeRow(
            singleHourWidth = singleHourWidth,
            onHeightUpdated = { timeRowHeight = it },
        )

        if (timeRowHeight != 0 && !listOffsets.isNullOrEmpty()) {
            with(LocalDensity.current) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = timeRowHeight.toDp() + 24.dp, bottom = 16.dp)
                        .height(boxHeight)
                ) {
                    listOffsets.forEach { (slot, pair) ->
                        Slot(
                            subject = slot.subject,
                            offset = pair.first,
                            width = pair.second,
                            slotHeight = slotHeight,
                            subjectLabelHeight = subjectLabelHeight,
                            selected = true,
                            onClick = { onSubjectClick(slot.subject) }
                        )
                    }
                    state.currentTimeIndicatorOffset?.let {
                        CurrentTimeIndicator(
                            offset = it
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewTimetable() {
    SeraTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            Card(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)),
                colors = CardDefaults.outlinedCardColors(),
                elevation = CardDefaults.outlinedCardElevation(),
            ) {
                Timetable(
                    state = rememberTimetableState(),
                    list = listOf(
                        TimetableSlot(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            },
                            subject = Subject(
                                title = "Mathematics",
                                colorValue = Color(0xFF78909C).toArgb(),
                            )
                        ),
                        TimetableSlot(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 10)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            },
                            subject = Subject(
                                title = "Science",
                                colorValue = Color(0xFFEC407A).toArgb(),
                            )
                        ),
                    ),
                    onSubjectClick = {},
                )
            }
        }
    }
}
