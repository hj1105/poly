package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "workbooks")
class Workbook(
    @Column(nullable = false)
    val name: String,

    @Column(name = "teacher_id", nullable = false)
    val teacherId: Long
) : BaseEntity()