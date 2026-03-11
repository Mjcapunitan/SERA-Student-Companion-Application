package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sera.R
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.RecurringSchedule
import com.example.sera.common.value_objects.schedule.Schedule
import com.example.sera.common.value_objects.schedule.getDaySchedules
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
fun SubjectListItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val color = remember(subject.colorValue) { subject.getColor() }
    Card(
        border = CardDefaults.outlinedCardBorder(),
        colors = CardDefaults.outlinedCardColors(),
        elevation = CardDefaults.outlinedCardElevation(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
                .then(modifier)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(24.dp)
                    .background(color = color, shape = RoundedCornerShape(50))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = subject.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subject.getSchedulesString()
                        ?: stringResource(id = R.string.no_schedule_set),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ChooseSubjectListItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onClick: () -> Unit,
) {
    val color = remember(subject.colorValue) { subject.getColor() }
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .size(8.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = subject.title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewSubjectListItem() {
    SeraTheme {
        SubjectListItem(
            subject = Subject(
                title = "Subject 1",
                daySchedulesMap = listOf(
                    RecurringSchedule(
                        Schedule(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 7)
                                set(Calendar.MINUTE, 0)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 0)
                            }
                        ),
                        days = Days(mutableSetOf(Day.MONDAY, Day.TUESDAY))
                    ),
                    RecurringSchedule(
                        schedule = Schedule(
                            from = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 30)
                            },
                            to = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 10)
                                set(Calendar.MINUTE, 30)
                            }
                        ),
                        days = Days(
                            mutableSetOf(Day.THURSDAY, Day.FRIDAY)
                        )
                    )
                ).getDaySchedules(),
            ),
            onClick = {},
            onLongClick = {},
        )
    }
}