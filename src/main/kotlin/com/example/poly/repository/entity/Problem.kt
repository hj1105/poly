package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import com.example.poly.common.enums.ProblemType
import jakarta.persistence.*

@Entity
@Table(
    name = "problems",
    indexes = [
        Index(name = "idx_unit_code", columnList = "unitCode"),
        Index(name = "idx_level", columnList = "level"),
        Index(name = "idx_problem_type", columnList = "problemType"),
        Index(name = "idx_deleted_at", columnList = "deletedAt")
    ]
)
class Problem(
    @Column(nullable = false)
    val content: String,

    @Column(nullable = false)
    val answer: String,

    @Column(nullable = false)
    val unitCode: String,

    @Column(nullable = false)
    val level: Int,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val problemType: ProblemType
) : BaseEntity()