package io.mocha.duty.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val name: String,
    val dutiesCount: Int
)
