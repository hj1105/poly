package com.example.poly.domain

data class ProblemAnswerStat(
    val problemId: Long,
    val correctCount: Long,
    val totalCount: Long
) {
    val correctRate: Double
        get() = if (totalCount > 0) correctCount.toDouble() / totalCount else 0.0
}