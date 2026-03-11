package com.example.sera.screens.subjects.subject_info

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack

import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.RecurringSchedule
import com.example.sera.common.value_objects.schedule.Schedule
import com.example.sera.common.value_objects.schedule.getDaySchedules
import com.example.sera.common.value_objects.schedule.getRecurringSchedules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SubjectInfoScreen(
    onBack: () -> Unit,
    isAdd: Boolean,
    subjectId: Int,
    viewModel: SubjectInfoViewModel = hiltViewModel()
) {
    var isEdit by rememberSaveable { mutableStateOf(isAdd) }
    val subject = if (subjectId == -1) {
        rememberSaveable { Subject() }
    } else {
        viewModel.observeSubject(subjectId).collectAsState(initial = null).value
    }

    if (subject != null) {
        SubjectInfoScreen(
            onBack = onBack,
            isAdd = isAdd,
            subject = subject,
            isEdit = isEdit,
            onSetEdit = { isEdit = it },
            onInsert = { viewModel.insertSubject(it) },
            onUpdate = { viewModel.updateSubject(it) },
            onDelete = { viewModel.deleteSubject(it) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectInfoScreen(
    onBack: () -> Unit,
    isAdd: Boolean,
    subject: Subject = Subject(),
    isEdit: Boolean = false,
    onSetEdit: ((Boolean) -> Unit),
    onInsert: (Subject) -> Unit,
    onUpdate: (Subject) -> Unit,
    onDelete: (Subject) -> Unit,
) {
    var editSubjectState by rememberSaveable(subject) { mutableStateOf(subject) }
    val recurringSchedulesState = rememberSaveable(
        isEdit,
        subject,
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        subject.daySchedulesMap.getRecurringSchedules().toMutableStateList()
    }

    var isTitleError by rememberSaveable { mutableStateOf(false) }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        flingAnimationSpec = decayAnimationSpec,
        state = rememberTopAppBarState(),
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = if (isAdd) stringResource(id = R.string.add_subject)
                        else if (isEdit) stringResource(R.string.edit_subject)
                        else stringResource(R.string.subject_info)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.CD_back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = if (isAdd) {
                            {
                                isTitleError = editSubjectState.title.isEmpty()

                                if (!isTitleError) {
                                    onInsert(editSubjectState.copy(daySchedulesMap = recurringSchedulesState.getDaySchedules()))
                                    onBack()
                                }
                            }
                        } else {
                            {
                                isTitleError = editSubjectState.title.isEmpty()

                                if (!isTitleError) {
                                    if (isEdit) onUpdate(editSubjectState.copy(daySchedulesMap = recurringSchedulesState.getDaySchedules()))
                                    onSetEdit(!isEdit)
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (isEdit) Icons.Outlined.Check else Icons.Outlined.Edit,
                            contentDescription = if (isEdit) stringResource(R.string.CD_save_subject) else stringResource(
                                R.string.CD_edit_subject
                            ),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(24.dp),
        ) {
            if (isAdd || isEdit) {
                item {
                    EditSubject(
                        subject = editSubjectState,
                        recurringSchedules = recurringSchedulesState,
                        isAdd = isAdd,
                        onUpdateSubject = { editSubjectState = it },
                        onDeleteSubject = {
                            onDelete(subject)
                            onBack()
                        },
                        isTitleError = isTitleError,
                        onSetIsTitleError = { isTitleError = it },
                    )
                }
            } else {
                item {
                    SubjectDetails(subject = subject)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSubject() {
    SeraTheme {
        SubjectInfoScreen(
            onBack = {},
            isAdd = false,
            subject = Subject(
                title = "Subject 3", daySchedulesMap = listOf(
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
                        days = Days(set = mutableSetOf(Day.MONDAY, Day.TUESDAY))
                    ),
                ).getDaySchedules()
            ),
            onSetEdit = {},
            onInsert = {},
            onUpdate = {},
            onDelete = {},
        )
    }
}

//@Preview
//@Composable
//fun PreviewAddSubject() {
//    SchoolTheme {
//        SubjectInfoScreen(
//            onBack = {},
//            isAdd = true,
//            onInsert = {},
//            onUpdate = {},
//        )
//    }
//}

@Preview
@Composable
fun PreviewEditSubject() {
    SeraTheme {
        SubjectInfoScreen(
            onBack = {},
            isAdd = false,
            subject = Subject(
                title = "Subject 3", daySchedulesMap = listOf(
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
                        days = Days(set = mutableSetOf(Day.MONDAY, Day.TUESDAY))
                    ),
                ).getDaySchedules()
            ),
            isEdit = true,
            onSetEdit = {},
            onInsert = {},
            onUpdate = {},
            onDelete = {},
        )
    }
}