package io.mocha.duty.data

import kotlinx.datetime.*

fun getCurrentLocalDate(): String {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.format(LocalDate.Formats.ISO)
}