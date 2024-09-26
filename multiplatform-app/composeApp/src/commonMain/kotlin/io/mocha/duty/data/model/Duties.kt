package io.mocha.duty.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Duties(
    val calender: Pair<String, Duty>,
    val students: List<Student>
)
