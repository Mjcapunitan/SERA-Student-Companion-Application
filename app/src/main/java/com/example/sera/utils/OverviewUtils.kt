package com.example.sera.utils

import android.content.SharedPreferences
import com.example.sera.R
import com.example.sera.common.Sera

fun getNickname(sharedPreferences: SharedPreferences): String? {
    return sharedPreferences.getString(Sera.NICKNAME, "")
}

fun getGreeting(hourOfDay: Int): Int {
    return when (hourOfDay) {
        in 5..11 -> {
            R.string.good_morning
        }
        in 12..16 -> {
            R.string.good_afternoon
        }
        else -> {
            R.string.good_evening
        }
    }
}
