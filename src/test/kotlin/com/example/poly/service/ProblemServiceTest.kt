package com.example.poly.service

import com.example.poly.common.enums.Level
import com.example.poly.common.enums.ProblemType
import com.example.poly.repository.ProblemQueryRepository
import com.example.poly.repository.ProblemRepository
import com.example.poly.repository.entity.Problem
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.cache.CacheManager
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class ProblemServiceTest {
    @MockK
    private lateinit var cacheManager: CacheManager

    private lateinit var problemService: ProblemService

    @MockK
    private lateinit var problemQueryRepository: ProblemQueryRepository

    @BeforeEach
    fun setUp() {
        problemService = ProblemService(
            problemQueryRepository = problemQueryRepository,
            cacheManager = cacheManager
        )
    }

    @Test
    fun `문제 조회 - 성공`() {
        // given
        val totalCount = 10
        val unitCodes = listOf("UNIT001", "UNIT002")
        val level = Level.HIGH
        val problemType = ProblemType.ALL

        val problems = listOf(
            createProblem(1L, "문제1", "답1", "UNIT001", 5, ProblemType.SUBJECTIVE),
            createProblem(2L, "문제2", "답2", "UNIT002", 3, ProblemType.SELECTION)
        )

        every {
            problemQueryRepository.findProblemsBy(
                totalCount = totalCount,
                unitCodes = unitCodes,
                level = level,
                problemType = problemType
            )
        } returns problems

        // when
        val result = problemService.findProblems(
            totalCount = totalCount,
            unitCodes = unitCodes,
            level = level,
            problemType = problemType
        )

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)

        verify(exactly = 1) {
            problemQueryRepository.findProblemsBy(
                totalCount = totalCount,
                unitCodes = unitCodes,
                level = level,
                problemType = problemType
            )
        }
    }

    @Test
    fun `문제 조회 - totalCount가 범위를 벗어난 경우 예외 발생`() {
        // given
        val totalCount = 51
        val unitCodes = listOf("UNIT001")
        val level = Level.HIGH
        val problemType = ProblemType.ALL

        // when & then
        assertThrows<IllegalArgumentException> {
            problemService.findProblems(
                totalCount = totalCount,
                unitCodes = unitCodes,
                level = level,
                problemType = problemType
            )
        }

        verify(exactly = 0) {
            problemQueryRepository.findProblemsBy(any(), any(), any(), any())
        }
    }

    private fun createProblem(
        id: Long,
        content: String,
        answer: String,
        unitCode: String,
        level: Int,
        problemType: ProblemType
    ) = Problem(
        content = content,
        answer = answer,
        unitCode = unitCode,
        level = level,
        problemType = problemType
    ).apply {
        ReflectionTestUtils.setField(this, "id", id)
    }
}