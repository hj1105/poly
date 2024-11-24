package com.example.poly.service

import com.example.poly.common.enums.Level
import com.example.poly.common.enums.ProblemType
import com.example.poly.dto.response.ProblemResponse
import com.example.poly.repository.ProblemQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProblemService(
    private val problemQueryRepository: ProblemQueryRepository,
    private val cacheManager: CacheManager
) {
    @Cacheable(
        value = ["problems"],
        key = "#totalCount + '_' + #unitCodes + '_' + #level + '_' + #problemType",
        unless = "#result.isEmpty()"
    )
    fun findProblems(
        totalCount: Int,
        unitCodes: List<String>,
        level: Level,
        problemType: ProblemType
    ): List<ProblemResponse> {
        require(totalCount in 1..50) { "Total count must be between 1 and 50" }

        val problems = problemQueryRepository.findProblemsBy(
            totalCount = totalCount,
            unitCodes = unitCodes,
            level = level,
            problemType = problemType
        )

        if (problems.size < totalCount) {
            logger.warn(
                "Found fewer problems({}) than requested({}). " +
                        "UnitCodes: {}, Level: {}, ProblemType: {}",
                problems.size, totalCount, unitCodes, level, problemType
            )
        }

        return problems.map { ProblemResponse.from(it) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ProblemService::class.java)
    }
}