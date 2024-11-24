package com.example.poly.repository

import com.example.poly.domain.ProblemAnswerStat
import com.example.poly.repository.entity.ProblemAnswer
import com.example.poly.repository.entity.QProblemAnswer
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProblemAnswerQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllByStudentWorkbookIds(studentWorkbookIds: List<Long>): List<ProblemAnswer> {
        return queryFactory
            .selectFrom(QProblemAnswer.problemAnswer)
            .where(QProblemAnswer.problemAnswer.studentWorkbookId.`in`(studentWorkbookIds))
            .fetch()
    }

    fun findProblemStatsByStudentWorkbookIds(studentWorkbookIds: List<Long>): List<ProblemAnswerStat> {
        return queryFactory
            .select(
                Projections.constructor(
                    ProblemAnswerStat::class.java,
                    QProblemAnswer.problemAnswer.problemId,
                    QProblemAnswer.problemAnswer.isCorrect.count(),
                    QProblemAnswer.problemAnswer.count()
                )
            )
            .from(QProblemAnswer.problemAnswer)
            .where(QProblemAnswer.problemAnswer.studentWorkbookId.`in`(studentWorkbookIds))
            .groupBy(QProblemAnswer.problemAnswer.problemId)
            .fetch()
    }
}