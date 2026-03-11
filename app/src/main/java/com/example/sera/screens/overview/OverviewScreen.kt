package com.example.sera.screens.overview


import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.sera.common.value_objects.ScheduleViewButtonItem
import com.example.sera.common.value_objects.entities.Subject
import com.example.sera.screens.DrawerContent
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.sera.R
import com.example.sera.components.*
import com.example.sera.utils.getGreeting
import com.example.sera.common.compose_ui.components.TimeIndicatorValue
import com.example.sera.common.compose_ui.components.Timetable
import com.example.sera.common.compose_ui.components.rememberTimetableState
import com.example.sera.utils.*
import com.example.sera.common.value_objects.*
import com.example.sera.common.value_objects.schedule.Schedule


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun OverviewScreen(
    navController: NavHostController,
    onGoToSubject: (Subject) -> Unit,
    nickname: String?,
    viewModel: OverviewViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController = navController, drawerState = drawerState)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Agenda") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OverviewScreen(
                    scrollState = scrollState,
                    onGoToSubject = onGoToSubject,
                    nickname = nickname,
                    hourOfDay = viewModel.hourOfDay,
                    dateToday = viewModel.dateTodayString,
                    message = viewModel.messageRes.collectAsState().value,
                    subjects = viewModel.allSubjects.collectAsState(initial = listOf()).value ?: listOf(),
                    onTimetableCurrentTimeUpdate = viewModel::updateTime,
                    timetableDone = viewModel.timetableDone.collectAsState(initial = null).value,
                    scheduleView = viewModel.scheduleView.value,
                    onSetScheduleView = viewModel::setScheduleView

                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
private fun OverviewScreen(
    scrollState: ScrollState,
    onGoToSubject: (Subject) -> Unit,
    nickname: String?,
    hourOfDay: Int,
    dateToday: String,
    @StringRes message: Int?,
    subjects: List<Subject>,
    onTimetableCurrentTimeUpdate: (Calendar?) -> Unit,
    timetableDone: Boolean?,
    scheduleView: ScheduleViewButtonItem,
    onSetScheduleView: (ScheduleViewButtonItem) -> Unit,
    modifier: Modifier = Modifier

) {

    val timetableSlots = remember(subjects.hashCode()) {
        mutableListOf<TimetableSlot>().apply {
            subjects.forEach { subject ->
                val day = getDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
                if (day != null) {
                    val schedules = subject.daySchedulesMap[day]
                    schedules?.forEach {
                        add(
                            TimetableSlot(
                                from = it.from,
                                to = it.to,
                                subject = subject,
                            )
                        )
                    }
                }
            }
        }
    }

    val timetableState = rememberTimetableState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(timetableState.timeIndicatorValue) {
        when (timetableState.timeIndicatorValue) {
            TimeIndicatorValue.Visible -> timetableState.startCurrentTimeIndicatorJob()
            TimeIndicatorValue.Hidden -> timetableState.stopCurrentTimeIndicatorJob()
        }.exhaustive
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                when (timetableState.timeIndicatorValue) {
                    TimeIndicatorValue.Visible -> coroutineScope.launch { timetableState.startCurrentTimeIndicatorJob() }
                    TimeIndicatorValue.Hidden -> coroutineScope.launch { timetableState.stopCurrentTimeIndicatorJob() }
                }.exhaustive
            }
            else -> {}
        }
    }

    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        timetableState.scrollToCurrentTime(density)
    }

    val timetableTime by timetableState.time.collectAsState()
    LaunchedEffect(timetableTime) {
        onTimetableCurrentTimeUpdate(timetableTime)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AgendaGreeting(getGreeting(hourOfDay), nickname)
        AgendaDateToday(dateToday)
        Spacer(modifier = Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.ViewDay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.classes),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(modifier = Modifier.weight(2f)) {
                SegmentedButton(
                    items = listOf(
                        ScheduleViewButtonItem.List,
                        ScheduleViewButtonItem.Timetable
                    ),
                    selected = scheduleView,
                    onSelectItem = { onSetScheduleView(it) },
                )
            }
        }

        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(),
        ) {
            if (timetableDone == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(0.dp, 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                AnimatedVisibility(visible = timetableSlots.isNotEmpty() && timetableDone) {
                    AllClassesDoneCard()
                }
                AnimatedVisibility(visible = timetableSlots.isEmpty()) {
                    NoClassesCard()
                }
                AnimatedVisibility(
                    visible = scheduleView == ScheduleViewButtonItem.List && timetableSlots.isNotEmpty() && !timetableDone,
                ) {
                    ScheduleList(
                        currentTime = timetableState.time.collectAsState().value
                            ?: Calendar.getInstance(),  // TODO
                        timetableSlots = timetableSlots,
                        onGoToSubject = onGoToSubject,
                    )
                }
                AnimatedVisibility(
                    visible = scheduleView == ScheduleViewButtonItem.Timetable && timetableSlots.isNotEmpty() && !timetableDone,
                ) {
                    Card(
                        modifier = Modifier.padding(top = 16.dp),
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f),
                        ),
                        colors = CardDefaults.outlinedCardColors(),
                        elevation = CardDefaults.outlinedCardElevation(),
                    ) {
                        Column(
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Timetable(
                                state = timetableState,
                                list = timetableSlots,
                                onSubjectClick = onGoToSubject,
                            )
                        }
                    }
                }
            }
        }
    }
}






