package com.example.sera.screens.subjects.subject_info


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.sera.R
import com.example.sera.common.compose_ui.components.TextFieldWithError
import com.example.sera.common.compose_ui.components.DeleteConfirmationDialog
import com.example.sera.common.compose_ui.components.rememberDeleteConfirmationDialogState
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.components.ColorPickerDialog
import com.example.sera.components.ColorsRow
import com.example.sera.components.EditScheduleCard
import com.example.sera.utils.Icons
import com.example.sera.common.Sera
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.RecurringSchedule
import com.example.sera.common.value_objects.schedule.Schedule
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun EditSubject(
    subject: Subject,
    recurringSchedules: SnapshotStateList<RecurringSchedule>,
    isAdd: Boolean,
    onUpdateSubject: (Subject) -> Unit,
    onDeleteSubject: (Int) -> Unit,
    isTitleError: Boolean,
    onSetIsTitleError: (Boolean) -> Unit,
) {
    var isColorSelected by remember { mutableStateOf(!isAdd) }
    val selectedColor = with(SeraTheme.colors.subjectColors) {
        remember(subject.colorValue) {
            if (!isColorSelected) first().also {
                onUpdateSubject(subject.copy(colorValue = it.toArgb()))
            } else subject.getColor()
        }
    }
    val deleteConfirmationDialogState = rememberDeleteConfirmationDialogState<Subject>()

    val (showColorPickerDialog, onSetShowColorPickerDialog) = remember { mutableStateOf(false) }

    if (showColorPickerDialog) {
        ColorPickerDialog(
            onSetShowColorPickerDialog = onSetShowColorPickerDialog,
            onSetSelectedColor = {
                onUpdateSubject(subject.copy(colorValue = it.toArgb()))
            },
        )
    }

    DeleteConfirmationDialog(
        state = deleteConfirmationDialogState,
        title = { "${stringResource(id = R.string.delete)} ${it.title}?" },
        text = { stringResource(id = R.string.are_you_sure_you_want_to_delete_this_subject) },
        onConfirmDelete = {
            onDeleteSubject(it.id)
        }
    )
    SubjectInfoEditor(
        subject = subject,
        onUpdateSubject = onUpdateSubject,
        isTitleError = isTitleError,
        onSetIsTitleError = onSetIsTitleError,
    )
    Spacer(modifier = Modifier.height(24.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.color),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(4.dp)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(percent = 50),
                ),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .padding(8.dp, 0.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        ColorsRow(
            selectedColor = selectedColor,
            onSetSelectedColor = {
                isColorSelected = true
                onUpdateSubject(subject.copy(colorValue = it.toArgb()))
            },
            onSetShowColorPickerDialog = onSetShowColorPickerDialog,
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(id = R.string.schedule),
        style = MaterialTheme.typography.titleSmall
    )
    Spacer(modifier = Modifier.height(8.dp))
    Column {
        val dateFormat =
            remember { SimpleDateFormat(Sera.timeFormat, Locale.getDefault()) }
        recurringSchedules.forEachIndexed { index, recurringSchedule ->
            val daysToDisable = mutableSetOf<Day>()
            recurringSchedules.filter {
                (it != recurringSchedule && checkOverlap(
                    recurringSchedule.schedule,
                    it.schedule
                ))
            }.map { it.days.set }.forEach { daysToDisable.addAll(it) }
            EditScheduleCard(
                time = "${dateFormat.format(recurringSchedule.schedule.from.time)} - ${
                    dateFormat.format(recurringSchedule.schedule.to.time)
                }",
                onSetSchedule = { schedule ->
                    val recurringScheduleCopy = recurringSchedule.copy(schedule = schedule)
                    recurringSchedules.removeOverlappingSchedule(scheduleToKeep = recurringScheduleCopy)
                    recurringSchedules[index] = recurringScheduleCopy
                },
                days = recurringSchedule.days.set,
                daysToDisable = daysToDisable,
                room = recurringSchedule.schedule.room,
                onUpdateRoom = { recurringSchedules[index] = recurringSchedule.copy(schedule = recurringSchedule.schedule.copy(room = it)) },
                onDayClick = {
                    recurringSchedules[index] = recurringSchedule.copy(
                        days = recurringSchedule.days.copyAndToggle(it)
                    )
                },
                onRemove = {
                    recurringSchedules.removeAt(index)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    TextButton(
        contentPadding = PaddingValues(
            start = 16.dp,
            top = ButtonDefaults.TextButtonContentPadding.calculateTopPadding(),
            end = 24.dp,
            bottom = ButtonDefaults.TextButtonContentPadding.calculateBottomPadding(),
        ),
        onClick = {
            val calendar = Calendar.getInstance()
            recurringSchedules.add(
                RecurringSchedule(
                    schedule = Schedule(
                        from = Calendar.getInstance().apply {
                            timeInMillis = 0
                            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, 0)
                            set(Calendar.MILLISECOND, 0)
                        },
                        to = Calendar.getInstance().apply {
                            timeInMillis = 0
                            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, 0)
                            set(Calendar.MILLISECOND, 0)
                            add(Calendar.HOUR_OF_DAY, 1)
                        }
                    ),
                    days = Days()
                )
            )
        },
    ) {
        Icon(
            imageVector = Icons.Add,
            contentDescription = stringResource(id = R.string.CD_add_schedule),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = R.string.add))
    }
    if (!isAdd) {
        HorizontalDivider(modifier = Modifier.padding(0.dp, 8.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { deleteConfirmationDialogState.show(subject) },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = ButtonDefaults.TextButtonContentPadding.calculateTopPadding(),
                end = 24.dp,
                bottom = ButtonDefaults.TextButtonContentPadding.calculateBottomPadding(),
            ),
        ) {
            Icon(
                imageVector = Icons.Delete,
                contentDescription = stringResource(id = R.string.CD_delete_subject),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.delete))
        }
    }
}


@Composable
private fun SubjectInfoEditor(
    subject: Subject,
    onUpdateSubject: (Subject) -> Unit,
    isTitleError: Boolean,
    onSetIsTitleError: (Boolean) -> Unit,
) {
    TextFieldWithError(
        value = subject.title,
        onValueChange = {
            onSetIsTitleError(it.isEmpty())
            onUpdateSubject(subject.copy(title = it))
        },
        labelText = stringResource(id = R.string.subject),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        modifier = Modifier.fillMaxWidth(),
        isError = isTitleError,
        onKeyboardAction = {},
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = subject.virtualMeetLink ?: "",
        onValueChange = {
            onUpdateSubject(subject.copy(virtualMeetLink = it))
        },
        label = {
            Text(text = stringResource(id = R.string.virtual_meet_link), color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        singleLine = false,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
        modifier = Modifier.fillMaxWidth()
    )
}

fun checkOverlap(schedule1: Schedule, schedule2: Schedule): Boolean {
    return max(
        schedule1.from.timeInMillis,
        schedule2.from.timeInMillis
    ) < min(
        schedule1.to.timeInMillis,
        schedule2.to.timeInMillis
    )
}

fun MutableList<RecurringSchedule>.removeOverlappingSchedule(scheduleToKeep: RecurringSchedule) {
    ArrayList(this).forEachIndexed { index, recurringSchedule ->
        if (recurringSchedule != scheduleToKeep && checkOverlap(
                scheduleToKeep.schedule,
                recurringSchedule.schedule
            )
        ) {
            var days = recurringSchedule.days.copy()
            recurringSchedule.days.set.forEach {
                if (scheduleToKeep.days.set.contains(it)) {
                    days = days.copyAndToggle(it)
                }
            }
            this[index] = recurringSchedule.copy(days = days)
        }
    }
}
