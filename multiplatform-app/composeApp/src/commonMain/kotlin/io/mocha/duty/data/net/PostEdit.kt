package io.mocha.duty.data.net

import kotlinx.serialization.Serializable

@Serializable
data class PostEdit(
    val date: String,
    val assigned: String? = null,
    val isDutyNeeded: Boolean,
    val password: String
)
