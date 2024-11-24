package com.example.poly.dto.request

import com.example.poly.common.enums.Level
import com.example.poly.common.enums.ProblemType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ProblemSearchRequest(
    @field:Min(1) @field:Max(50)
    val totalCount: Int,

    @field:NotEmpty
    val unitCodeList: List<String>,

    @field:NotNull
    val level: Level,

    @field:NotNull
    val problemType: ProblemType
)
