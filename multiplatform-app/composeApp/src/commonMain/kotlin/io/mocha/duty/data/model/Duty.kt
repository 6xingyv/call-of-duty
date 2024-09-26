package io.mocha.duty.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Duty(
    val assigned: String?,
    val isDutyNeeded: Boolean
)
