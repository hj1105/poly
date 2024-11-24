package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import com.example.poly.common.enums.WorkbookProblemStatus
import jakarta.persistence.*

@Entity
@Table(
    name = "workbook_problems",
    indexes = [
        Index(name = "idx_workbook_id", columnList = "workbookId"),
        Index(name = "idx_problem_id", columnList = "problemId"),
        Index(name = "idx_deleted_at", columnList = "deletedAt")
    ]
)
class WorkbookProblem(
    @Column(nullable = false)
    val workbookId: Long,

    @Column(nullable = false)
    val problemId: Long,

    @Column(nullable = false)
    val orderNumber: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: WorkbookProblemStatus = WorkbookProblemStatus.ACTIVE
) : BaseEntity() {
    fun deactivate() {
        this.status = WorkbookProblemStatus.INACTIVE
        delete()
    }
}