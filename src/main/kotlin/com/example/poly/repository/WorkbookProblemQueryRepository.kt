package com.example.poly.repository

import com.example.poly.common.enums.WorkbookProblemStatus
import com.example.poly.repository.entity.Problem
import com.example.poly.repository.entity.QProblem
import com.example.poly.repository.entity.QWorkbookProblem
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class WorkbookProblemQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findActiveProblemsInWorkbook(workbookId: Long): List<Problem> {
        return queryFactory
            .selectFrom(QProblem.problem)
            .join(QWorkbookProblem.workbookProblem)
            .on(QWorkbookProblem.workbookProblem.problemId.eq(QProblem.problem.id))
            .where(
                QWorkbookProblem.workbookProblem.workbookId.eq(workbookId)
                    .and(QWorkbookProblem.workbookProblem.status.eq(WorkbookProblemStatus.ACTIVE))
                    .and(QWorkbookProblem.workbookProblem.deletedAt.isNull)
                    .and(QProblem.problem.deletedAt.isNull)
            )
            .orderBy(QWorkbookProblem.workbookProblem.orderNumber.asc())
            .fetch()
    }
}