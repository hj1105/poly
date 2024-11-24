package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "student_workbooks",
    indexes = [
        Index(name = "idx_student_id", columnList = "studentId"),
        Index(name = "idx_workbook_id", columnList = "workbookId")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_student_workbook",
            columnNames = ["studentId", "workbookId"]
        )
    ]
)
class StudentWorkbook(
    @Column(nullable = false)
    val studentId: Long,

    @Column(nullable = false)
    val workbookId: Long
) : BaseEntity()