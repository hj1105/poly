package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "problem_answers",
    indexes = [
        Index(name = "idx_student_workbook_id", columnList = "studentWorkbookId"),
        Index(name = "idx_problem_id", columnList = "problemId")
    ]
)
class ProblemAnswer(
    @Column(nullable = false)
    val studentWorkbookId: Long,

    @Column(nullable = false)
    val problemId: Long,

    @Column(nullable = false)
    val studentAnswer: String,

    @Column(nullable = false)
    val isCorrect: Boolean
) : BaseEntity()