package com.example.sera.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.sera.R
import com.example.sera.screens.notepad.AddEditScreen
import com.example.sera.screens.notepad.NotepadListScreen
import com.example.sera.screens.overview.OverviewScreen
import com.example.sera.screens.question.GenerateQuizScreen
import com.example.sera.screens.question.GenerateQuizViewModel
import com.example.sera.screens.question.QListScreen
import com.example.sera.screens.question.QuizAnsweringScreen
import com.example.sera.screens.saved.SavedSummariesScreen
import com.example.sera.screens.saved.SummaryDetailScreen
import com.example.sera.screens.stats.QuizDetailScreen
import com.example.sera.screens.stats.QuizStatisticsScreen
import com.example.sera.screens.subjects.subject_info.SubjectInfoScreen
import com.example.sera.screens.subjects.subjects_list.SubjectsListScreen
import com.example.sera.screens.summarize.SummarizeScreen
import com.example.sera.screens.summarize.SummaryDataStore
import com.example.sera.screens.summarize.SummaryResultScreen
import com.example.sera.screens.timetable.TimetableScreen
import com.example.sera.screens.stats.PerformanceAssessmentScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

val items = listOf(
    Triple("Agenda", R.drawable.baseline_overview_24, "overview"),
    Triple("Subjects", R.drawable.baseline_subjects_24, "subjects_list"),
    Triple("Timetable", R.drawable.baseline_timetable_24, "timetable"),
    Triple("Notepad", R.drawable.baseline_notepad_24, "notepad_list"),
    Triple("Summarize Lecture", R.drawable.baseline_summarize_lec_24, "summarize"),
    Triple("Generate Quiz", R.drawable.baseline_history_quiz_24, "generate_quiz"),
    Triple("Saved Summaries", R.drawable.baseline_history_24, "saved_summary"),
    Triple("Quiz Statistics", R.drawable.statistics, "stats"),
    Triple("Personal Assessment", R.drawable.evaluation, "assessment")

)

@Composable
fun Screen() {
    val navController = rememberNavController()
    NavigationHost(navController = navController)
}

@Composable
fun DrawerContent(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route
    Column(

        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(

                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = "SERA Companion App",
                    fontSize = 18.sp
                )
                Text(
                    text = "Study Efficiently Reach Achievements",
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { (label, icon, route) ->
            NavigationDrawerItem(
                label = { Text(label) },
                icon = {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "$label Icon"
                    )
                },
                selected = currentRoute == route, // Highlight if current route matches the item's route
                onClick = {
                    scope.launch {
                        drawerState.close() // Close drawer first
                        if (currentRoute != route) { // Prevent redundant navigation
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Highlight background
                    selectedTextColor = MaterialTheme.colorScheme.primary,                         // Highlighted text color
                    unselectedContainerColor = MaterialTheme.colorScheme.surface,                // Default background
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface                    // Default text color
                ),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController,
        startDestination = "overview"
    ) {
        composable("overview") { OverviewScreen(
            navController,
            nickname = null,
            onGoToSubject = { subject ->
                // Navigate to subject info screen when a timetable slot is clicked
                navController.navigate("subject_info/false?subjectId=${subject.id}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }

        ) }
        composable("timetable") {
            TimetableScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onGoToSubject = { subject ->
                    // Navigate to subject info screen when a timetable slot is clicked
                    navController.navigate("subject_info/false?subjectId=${subject.id}") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("summarize") {
            SummarizeScreen(
                navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable("summaryResult/{fileName}") { backStackEntry ->
            val encodedFileName = backStackEntry.arguments?.getString("fileName") ?: "Untitled"
            val fileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8.toString())
            val summary = SummaryDataStore.getSummary()


            SummaryResultScreen(
                summary = summary,
                fileName = fileName,
                onBack = { if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } }
            )
        }
        composable("saved_summary") {
            SavedSummariesScreen(
                onBack = { if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } },
                onSummaryClick = { summaryId ->
                    navController.navigate("summaryDetail/$summaryId")
                }
            )
        }

        composable(
            route = "summaryDetail/{summaryId}",
            arguments = listOf(navArgument("summaryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val summaryId = backStackEntry.arguments?.getLong("summaryId") ?: 0L

            SummaryDetailScreen(
                summaryId = summaryId,
                onBack = { if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } }
            )
        }

        // Add Notepad List screen
        composable("notepad_list") {
            NotepadListScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onNoteClick = { noteId ->
                    navController.navigate("edit_note/$noteId")
                },
                onAddNoteClick = {
                    navController.navigate("edit_note/0") // Navigate to Add/Edit screen with noteId = 0 (for new note)
                }
            )
        }

        composable(
            route = "edit_note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L

            AddEditScreen(
                navController = navController,
                noteId = noteId,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("generate_quiz") {
            GenerateQuizScreen(
                navController = navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("quiz_answering") {
            QuizAnsweringScreen(
                navController = navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            "generate_quiz?reset={reset}",
            arguments = listOf(navArgument("reset") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val shouldReset = backStackEntry.arguments?.getBoolean("reset") ?: false
            val viewModel: GenerateQuizViewModel = hiltViewModel()

            // Reset if coming from quiz with reset flag
            if (shouldReset) {
                LaunchedEffect(true) {
                    viewModel.resetState()
                }
            }

            GenerateQuizScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } },
                onNavigateToSummarize = {
                    navController.navigate("summarize")
                }
            )
        }

        composable("stats") {
            QuizStatisticsScreen(
                navController = navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("assessment") {
            PerformanceAssessmentScreen(
                navController = navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onGoToQuiz = {
                    navController.navigate("generate_quiz") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = "quiz_detail/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""

            QuizDetailScreen(
                navController = navController,
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                quizId = quizId
            )
        }

        addSubjectsNavigation(navController)
    }
}

// Function to manage subject-related screens
@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.addSubjectsNavigation(navController: NavHostController) {

    composable("subjects_list") {
        SubjectsListScreen(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            },
            onAddSubject = {
                navController.navigate("subject_info/true") {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onClickSubject = { subject ->
                navController.navigate("subject_info/false?subjectId=${subject.id}") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    composable(
        route = "subject_info/{isAdd}?subjectId={subjectId}",
        arguments = listOf(
            navArgument("isAdd") { type = NavType.BoolType },
            navArgument("subjectId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) {
        SubjectInfoScreen(
            onBack = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
            } },
            isAdd = it.arguments?.getBoolean("isAdd") ?: false,
            subjectId = it.arguments?.getInt("subjectId") ?: -1
        )
    }
}
