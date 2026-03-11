package com.example.sera.common.compose_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun TimeRow(
    singleHourWidth: Dp,
    onHeightUpdated: (Int) -> Unit,
) {
    Row {
        (0..24).forEach { hour ->
            Column(
                modifier = Modifier
                    .width(singleHourWidth)
                    .fillMaxHeight()
                    .padding(0.dp, 16.dp)
                    .onGloballyPositioned {
                        onHeightUpdated(it.size.height)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(
                    text = getHourText(hour),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .onGloballyPositioned {
                            onHeightUpdated(it.size.height)
                        },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(50),
                        )
                )
            }
        }
    }
}