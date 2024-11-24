package com.example.poly.repository

import com.example.poly.repository.entity.Problem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProblemRepository : JpaRepository<Problem, Long> {
}