package com.example.sera.utils

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.lang.reflect.Type
import com.google.gson.*
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType

val <T> T.exhaustive: T
    get() = this

fun showTimePicker(
    context: Context,
    @StringRes title: Int? = null,
    hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    minute: Int = 0,
    onTimeSet: (date: Calendar) -> Unit,
    onCancel: (() -> Unit)? = null,
) {
    val activity = context as? AppCompatActivity
    if (activity != null) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hourOfDay)
            .setMinute(minute)
            .setTitleText(title?.let { context.getString(it) } ?: "")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val date = Calendar.getInstance().apply {
                timeInMillis = 0
                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
            }
            onTimeSet(date)
        }
        timePicker.addOnCancelListener {
            onCancel?.invoke()
        }

        timePicker.show(activity.supportFragmentManager, "time_picker")
    } else {
        val calendar =
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val date = Calendar.getInstance().apply {
                        timeInMillis = 0
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                    onTimeSet(date)
                },
                hourOfDay,
                minute,
                false
            ).apply {
                title?.let { setTitle(context.getString(it)) }
                if (onCancel != null) {
                    setOnCancelListener {
                        onCancel()
                    }
                }
                show()
            }
    }
}

fun Number.toPx() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).toInt()

class UriSerializer : JsonSerializer<Uri?> {
    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class UriDeserializer : JsonDeserializer<Uri?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        src: JsonElement, srcType: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(src.asString)
    }
}

fun AlarmManager.exactAlarmsAllowed(): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        canScheduleExactAlarms()
    } else {
        true
    }
}