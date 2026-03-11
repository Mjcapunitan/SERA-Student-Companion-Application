package com.example.sera.screens.subjects.subject_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.sera.R
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sera.components.ScheduleView
import com.example.sera.utils.Icons
import com.example.sera.common.value_objects.entities.Subject

@Composable
fun SubjectDetails(subject: Subject) {
    val uriHandler = LocalUriHandler.current
    val color = remember(subject.colorValue) { subject.getColor() }
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(8.dp)
            .background(color = color, shape = RoundedCornerShape(50))
    )
    Text(text = subject.title, style = MaterialTheme.typography.headlineLarge)
    subject.virtualMeetLink.let { link ->
        if (!link.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(
                onClick = {
                    if (link.substring(startIndex = 0, endIndex = 8) == "https://") {
                        uriHandler.openUri(link)
                    } else {
                        uriHandler.openUri("https://$link")
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.VideoCall, contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.join))
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(id = R.string.schedule),
        style = MaterialTheme.typography.titleSmall
    )
    ScheduleView(
        modifier = Modifier.padding(top = 16.dp, start = 8.dp),
        schedules = subject.daySchedulesMap
    )
}