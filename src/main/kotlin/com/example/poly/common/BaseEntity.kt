package com.example.poly.common

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Column(nullable = false)
    var deletedAt: LocalDateTime? = null
        protected set

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        this.updatedAt = LocalDateTime.now()
    }
}