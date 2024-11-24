package com.example.poly.repository

import com.example.poly.common.enums.Level
import com.example.poly.common.enums.ProblemType
import com.example.poly.repository.entity.Problem
import com.example.poly.repository.entity.QProblem
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProblemQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findProblemsBy(
        totalCount: Int,
        unitCodes: List<String>,
        level: Level,
        problemType: ProblemType
    ): List<Problem> {
        val (lowCount, middleCount, highCount) = calculateProblemCounts(totalCount, level)

        val baseQuery = queryFactory
            .selectFrom(QProblem.problem)
            .where(
                QProblem.problem.unitCode.`in`(unitCodes),
                QProblem.problem.deletedAt.isNull
            )
            .apply {
                if (problemType != ProblemType.ALL) {
                    where(QProblem.problem.problemType.eq(problemType))
                }
            }

        val lowLevelProblems = baseQuery.clone()
            .where(QProblem.problem.level.eq(1))
            .limit(lowCount.toLong())
            .fetch()

        val middleLevelProblems = baseQuery.clone()
            .where(QProblem.problem.level.between(2, 4))
            .limit(middleCount.toLong())
            .fetch()

        val highLevelProblems = baseQuery.clone()
            .where(QProblem.problem.level.eq(5))
            .limit(highCount.toLong())
            .fetch()

        return lowLevelProblems + middleLevelProblems + highLevelProblems
    }

    private fun calculateProblemCounts(totalCount: Int, level: Level): Triple<Int, Int, Int> {
        return when (level) {
            Level.HIGH -> Triple(
                (totalCount * 0.2).toInt(),
                (totalCount * 0.3).toInt(),
                (totalCount * 0.5).toInt()
            )
            Level.MIDDLE -> Triple(
                (totalCount * 0.25).toInt(),
                (totalCount * 0.5).toInt(),
                (totalCount * 0.25).toInt()
            )
            Level.LOW -> Triple(
                (totalCount * 0.5).toInt(),
                (totalCount * 0.3).toInt(),
                (totalCount * 0.2).toInt()
            )
        }
    }
}