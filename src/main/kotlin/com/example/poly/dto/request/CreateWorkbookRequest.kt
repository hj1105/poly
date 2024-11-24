package com.example.poly.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateWorkbookRequest(
    @field:NotNull
    val teacherId: Long,

    @field:NotBlank
    val name: String,

    @field:Size(min = 1, max = 50)
    @field:NotEmpty
    val problemIds: List<Long>
)