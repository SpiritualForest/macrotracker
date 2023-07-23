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