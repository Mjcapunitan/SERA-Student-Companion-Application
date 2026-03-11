package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.util.*

enum class TimeIndicatorValue {
    Visible,
    Hidden,
}

class TimetableState(
    val scrollState: ScrollState,
    singleHourWidth: Dp,
    initialTimeIndicatorValue: TimeIndicatorValue,
) {
    private var currentTimeJob: Job? = null
    private var timeIndicatorJob: Job? = null

    private val _timeState = MutableStateFlow<Calendar?>(null)
    val time: StateFlow<Calendar?> = _timeState

    private val _singleHourWidthState = MutableStateFlow(singleHourWidth)
    val singleHourWidth: StateFlow<Dp> = _singleHourWidthState

    var currentTimeIndicatorOffset by mutableStateOf(
        if (initialTimeIndicatorValue == TimeIndicatorValue.Hidden) null
        else getStartXValue(Calendar.getInstance(), singleHourWidth)
    )
        private set

    var timeIndicatorValue by mutableStateOf(initialTimeIndicatorValue)

    fun updateSingleHourWidth(width: Dp) {
        _singleHourWidthState.value = width
    }

    suspend fun scrollToCurrentTime(density: Density) {
        with(density) {
            val offset = currentTimeIndicatorOffset
            if (offset != null) {
                scrollState.animateScrollTo((offset - 24.dp).toPx().toInt())
            }
        }
    }

    suspend fun startCurrentTimeIndicatorJob() {
        currentTimeJob?.cancelAndJoin()
        timeIndicatorJob?.cancelAndJoin()
        coroutineScope {
            currentTimeJob = launch {
                val calendar = Calendar.getInstance()
                _timeState.value = calendar
                delay((((60 - calendar.get(Calendar.SECOND)) * 1000) - calendar.get(Calendar.MILLISECOND)).toLong())
                while (isActive) {
                    var new = _timeState.value?.clone() as Calendar?
                    if (new == null) {
                        new = Calendar.getInstance().apply {
                            add(Calendar.MINUTE, 1)
                        }
                    } else {
                        new.add(Calendar.MINUTE, 1)
                    }
                    _timeState.value = new
                    delay(60000)
                }
            }
            timeIndicatorJob = launch {
                combine(_timeState, _singleHourWidthState) { time, singleHourWidth ->
                    currentTimeIndicatorOffset =
                        if (time == null) null else getStartXValue(time, singleHourWidth)
                }.collect()
                currentTimeIndicatorOffset = null
            }
        }
    }

    suspend fun stopCurrentTimeIndicatorJob() {
        currentTimeJob?.cancelAndJoin()
    }

    companion object {

        private const val scrollStateKey = "scroll_state"
        private const val singleHourWidthKey = "single_hour_width"
        private const val timeIndicatorValueKey = "time_indicator_value"

        val Saver: Saver<TimetableState, *> = mapSaver(
            save = {
                mapOf(
                    scrollStateKey to it.scrollState.value,
                    singleHourWidthKey to it.singleHourWidth.value.value,
                    timeIndicatorValueKey to it.timeIndicatorValue,
                )
            },
            restore = {
                TimetableState(
                    scrollState = ScrollState(it[scrollStateKey] as Int),
                    singleHourWidth = Dp(it[singleHourWidthKey] as Float),
                    initialTimeIndicatorValue = it[timeIndicatorValueKey] as TimeIndicatorValue,
                )
            }
        )
    }
}

@Composable
fun rememberTimetableState(
    scrollState: ScrollState = rememberScrollState(),
    singleHourWidth: Dp = 100.dp,
    initialTimeIndicatorValue: TimeIndicatorValue = TimeIndicatorValue.Visible,
): TimetableState =
    rememberSaveable(
        scrollState,
        singleHourWidth,
        initialTimeIndicatorValue,
        saver = TimetableState.Saver
    ) {
        TimetableState(
            scrollState = scrollState,
            singleHourWidth = singleHourWidth,
            initialTimeIndicatorValue = initialTimeIndicatorValue,
        )
    }
