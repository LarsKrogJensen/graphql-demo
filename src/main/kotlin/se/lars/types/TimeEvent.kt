package se.lars.types

import java.time.LocalDateTime


class TimeEvent {
    private val date = LocalDateTime.now()

    val year get() = date.year
    val month get() = date.month.value
    val day get() = date.dayOfMonth
    val hour get() = date.hour
    val minute get() = date.minute
    val seond get() = date.second
}