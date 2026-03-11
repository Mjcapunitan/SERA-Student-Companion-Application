package com.example.sera.common.value_objects.days

import android.os.Parcelable
import com.example.sera.common.value_objects.Day
import kotlinx.parcelize.Parcelize


@Parcelize
data class Days(val set: MutableSet<Day> = mutableSetOf()) : Parcelable {

    fun toggleDay(day: Day) {
        if (day in set) {
            set.remove(day)
        } else {
            set.add(day)
        }
    }

    fun copyAndToggle(day: Day): Days {
        val copy = this.copy(set = LinkedHashSet(set))
        copy.toggleDay(day)
        return copy
    }
}
