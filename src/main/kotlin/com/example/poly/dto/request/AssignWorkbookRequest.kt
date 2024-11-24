package com.example.poly.dto.request

import jakarta.validation.constraints.NotEmpty

data class AssignWorkbookRequest(
    @field:NotEmpty
    val studentIds: List<Long>
)