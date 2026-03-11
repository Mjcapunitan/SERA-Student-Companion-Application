package com.example.sera.screens.subjects.subjects_list

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sera.common.compose_ui.components.SubjectListItem
import com.example.sera.common.compose_ui.components.DeleteConfirmationDialog
import com.example.sera.common.compose_ui.components.rememberDeleteConfirmationDialogState
import com.example.sera.ui.theme.SeraTheme
import com.example.sera.R
import com.example.sera.common.value_objects.Day
import com.example.sera.common.value_objects.days.Days
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.common.value_objects.schedule.RecurringSchedule
import com.example.sera.common.value_objects.schedule.Schedule
import com.example.sera.common.value_objects.schedule.getDaySchedules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalFoundationApi
@Composable
fun SubjectsListScreen(
    onBack: () -> Unit,
    onAddSubject: () -> Unit,
    onClickSubject: (Subject) -> Unit,
    viewModel: SubjectsListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    SubjectsListScreen(
        onBack = onBack,
        onAddSubject = onAddSubject,
        onClickSubject = onClickSubject,
        onDeleteSubject = { viewModel.deleteSubject(it) },
        subjects = viewModel.observeAllSubjects().collectAsState(
            initial = listOf()
        ).value ?: listOf()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@Composable
private fun SubjectsListScreen(
    onBack: () -> Unit,
    onAddSubject: () -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onClickSubject: (Subject) -> Unit,
    subjects: List<Subject>
) {
    val deleteConfirmationDialogState = rememberDeleteConfirmationDialogState<Subject>()

    DeleteConfirmationDialog(
        state = deleteConfirmationDialogState,
        title = { "${stringResource(id = R.string.delete)} ${it.title}?" },
        text = { stringResource(id = R.string.are_you_sure_you_want_to_delete_this_subject) },
        onConfirmDelete = {
            onDeleteSubject(it)
        }
    )

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        flingAnimationSpec = decayAnimationSpec,
        state = rememberTopAppBarState(),
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.subjects)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.CD_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddSubject,
                text = { Text(text = stringResource(id = R.string.add_subject)) },
                icon = {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.CD_add_subject)
                    )
                },
            )
        },
    ) { padding ->
        if (subjects.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(24.dp),
            ) {
                items(subjects) { subject ->
                    SubjectListItem(
                        subject = subject,
                        onClick = { onClickSubject(subject) },
                        onLongClick = { deleteConfirmationDialogState.show(subject) },
                    )
                    if (subject != subjects.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.no_subjects_yet),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewSubjectsListScreen() {
    SeraTheme {
        SubjectsListScreen(
            onBack = {},
            onAddSubject = {},
            onClickSubject = {},
            onDeleteSubject = {},
            subjects = listOf(
                Subject(title = "Subject 1"),
                Subject(title = "Subject 2"),
                Subject(
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
                            days = Days(mutableSetOf(Day.MONDAY, Day.TUESDAY))
                        ),
                    ).getDaySchedules()
                ),
            )
        )
    }
}

