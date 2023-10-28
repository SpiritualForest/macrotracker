package com.macrotracker.database

import kotlinx.datetime.*

/* Various utility functions for converting date and time data around */

fun Int.toFormattedDate(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val localDate = LocalDate.fromEpochDays(this)
    val day = localDate.dayOfMonth
    val month = localDate.monthNumber
    val year = localDate.year
    return if (year == today.year) {
        "$day.$month"
    } else {
        "$day.$month.$year"
    }
}

fun todayEpochDays(): Int = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays()

fun tomorrowEpochDays(): Int = todayEpochDays() + 1

fun nowToSecondOfDay(): Int {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .time
        .toSecondOfDay()
}

fun nowEpochMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

// Basically: now() + 24 hours
fun tomorrowEpochMilliseconds(): Long = nowEpochMilliseconds() + (86400 * 1000)
