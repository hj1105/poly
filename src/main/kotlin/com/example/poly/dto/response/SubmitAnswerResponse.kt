package com.example.poly.dto.response

data class SubmitAnswersResponse(
    val correctCount: Int,
    val totalCount: Int,
    val correctRate: Double
)