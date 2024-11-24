package com.example.poly.repository

import com.example.poly.repository.entity.WorkbookProblem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkbookProblemRepository : JpaRepository<WorkbookProblem, Long> {
    fun findAllByWorkbookId(workbookId: Long): List<WorkbookProblem>
}