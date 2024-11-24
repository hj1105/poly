package com.example.poly.repository

import com.example.poly.repository.entity.ProblemAnswer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProblemAnswerRepository : JpaRepository<ProblemAnswer, Long> {
}