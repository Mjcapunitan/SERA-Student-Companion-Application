package com.example.sera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.*

@Composable
fun ColorPickerDialog(
    onSetShowColorPickerDialog: (Boolean) -> Unit,
    onSetSelectedColor: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()
    Dialog(onDismissRequest = { onSetShowColorPickerDialog(false) }) {
        var hexCode by remember { mutableStateOf("") }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(16.dp),
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .size(300.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    hexCode = colorEnvelope.hexCode
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrightnessSlider(
                modifier = Modifier
                    .width(300.dp)
                    .height(35.dp),
                controller = controller,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "#${hexCode}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AlphaTile(
                modifier = Modifier
                    .height(24.dp)
                    .width(100.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                controller = controller,
            )
            TextButton(
                onClick = {
                    onSetSelectedColor(controller.selectedColor.value)
                    onSetShowColorPickerDialog(false)
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(text = "done".uppercase())
            }
        }
    }
}