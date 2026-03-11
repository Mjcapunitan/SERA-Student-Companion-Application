package com.example.sera.utils

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*

typealias Icons = Icons.Outlined

@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycleOwner) {
    flow.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState
    )
}

fun Color.onColor()
        : Color {
    return if (luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
