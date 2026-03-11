package com.example.sera.utils

inline fun <reified E : Enum<E>, T> mutableMapForEnumWithValue(value: () -> T): MutableMap<E, T> {
    val map = mutableMapOf<E, T>()
    enumValues<E>().forEach { i ->
        map[i] = value()
    }
    return map
}

inline fun <reified E : Enum<E>, T> mutableMapForEnumWithValue(value: T): MutableMap<E, T> {
    val map = mutableMapOf<E, T>()
    enumValues<E>().forEach { i ->
        map[i] = value
    }
    return map
}