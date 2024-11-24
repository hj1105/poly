package com.example.poly.repository.entity

import com.example.poly.common.BaseEntity
import com.example.poly.common.enums.UserRole
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole
) : BaseEntity() {
    companion object {
        fun createTeacher(name: String) = User(name = name, role = UserRole.TEACHER)
        fun createStudent(name: String) = User(name = name, role = UserRole.STUDENT)
    }
}