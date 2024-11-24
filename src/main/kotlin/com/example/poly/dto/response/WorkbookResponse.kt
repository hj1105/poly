package com.example.poly.dto.response

import com.example.poly.repository.entity.Workbook
import java.time.LocalDateTime

data class WorkbookResponse(
    val id: Long,
    val name: String,
    val teacherId: Long,
    val problemCount: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(workbook: Workbook, problemCount: Int) = WorkbookResponse(
            id = workbook.id!!,
            name = workbook.name,
            teacherId = workbook.teacherId,
            problemCount = problemCount,
            createdAt = workbook.createdAt
        )
    }
}