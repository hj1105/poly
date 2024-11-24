package com.example.poly.dto.response

import com.example.poly.common.enums.ProblemType
import com.example.poly.repository.entity.Problem

data class ProblemResponse(
    val id: Long,
    val content: String,
    val answer: String,
    val unitCode: String,
    val level: Int,
    val problemType: ProblemType
) {
    companion object {
        fun from(problem: Problem) = ProblemResponse(
            id = problem.id!!,
            content = problem.content,
            answer = problem.answer,
            unitCode = problem.unitCode,
            level = problem.level,
            problemType = problem.problemType
        )
    }
}