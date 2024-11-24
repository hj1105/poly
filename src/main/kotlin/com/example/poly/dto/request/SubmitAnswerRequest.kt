package com.example.poly.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SubmitAnswerRequest(
    @field:NotNull
    val problemId: Long,

    @field:NotBlank
    val answer: String
)