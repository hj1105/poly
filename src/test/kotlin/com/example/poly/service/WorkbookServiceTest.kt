package com.example.poly.service

import com.example.poly.common.UnauthorizedException
import com.example.poly.common.WorkbookNotFoundException
import com.example.poly.common.enums.ProblemType
import com.example.poly.common.enums.UserRole
import com.example.poly.dto.request.AssignWorkbookRequest
import com.example.poly.dto.request.CreateWorkbookRequest
import com.example.poly.repository.*
import com.example.poly.repository.entity.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class WorkbookServiceTest {
    @MockK
    private lateinit var workbookRepository: WorkbookRepository

    @MockK
    private lateinit var workbookProblemRepository: WorkbookProblemRepository

    @MockK
    private lateinit var studentWorkbookRepository: StudentWorkbookRepository

    @MockK
    private lateinit var problemRepository: ProblemRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var problemAnswerRepository: ProblemAnswerRepository

    @MockK
    private lateinit var userQueryRepository: UserQueryRepository

    @MockK
    private lateinit var workbookProblemQueryRepository: WorkbookProblemQueryRepository

    @MockK
    private lateinit var studentWorkbookQueryRepository: StudentWorkbookQueryRepository

    @MockK
    private lateinit var problemAnswerQueryRepository: ProblemAnswerQueryRepository

    private lateinit var workbookService: WorkbookService

    @BeforeEach
    fun setUp() {
        workbookService = WorkbookService(
            workbookRepository = workbookRepository,
            workbookProblemRepository = workbookProblemRepository,
            studentWorkbookRepository = studentWorkbookRepository,
            problemRepository = problemRepository,
            userRepository = userRepository,
            userQueryRepository = userQueryRepository,
            workbookProblemQueryRepository = workbookProblemQueryRepository,
            studentWorkbookQueryRepository = studentWorkbookQueryRepository,
            problemAnswerRepository = problemAnswerRepository,
            problemAnswerQueryRepository = problemAnswerQueryRepository
        )
    }

    @Test
    fun `학습지 생성 - 성공`() {
        // given
        val request = CreateWorkbookRequest(
            teacherId = 1L,
            name = "테스트 학습지",
            problemIds = listOf(1L, 2L)
        )

        val teacher = createUser(1L, "선생님", UserRole.TEACHER)
        val problems = listOf(
            createProblem(1L),
            createProblem(2L)
        )
        val workbook = createWorkbook(1L, request.name, teacher.id!!)

        every { userQueryRepository.findByIdAndRole(1L, UserRole.TEACHER) } returns teacher
        every { problemRepository.findAllById(request.problemIds) } returns problems
        every { workbookRepository.save(any()) } returns workbook
        every {
            workbookProblemRepository.saveAll<WorkbookProblem>(any())
        } returns listOf()

        // when
        val result = workbookService.createWorkbook(request)

        // then
        assertThat(result.id).isEqualTo(workbook.id)
        assertThat(result.name).isEqualTo(request.name)
        assertThat(result.teacherId).isEqualTo(teacher.id)

        verify(exactly = 1) {
            userQueryRepository.findByIdAndRole(1L, UserRole.TEACHER)
            problemRepository.findAllById(request.problemIds)
            workbookRepository.save(any())
            workbookProblemRepository.saveAll<WorkbookProblem>(any())
        }
    }

    @Test
    fun `학습지 생성 - 교사가 아닌 경우 실패`() {
        // given
        val request = CreateWorkbookRequest(
            teacherId = 1L,
            name = "테스트 학습지",
            problemIds = listOf(1L, 2L)
        )

        every { userQueryRepository.findByIdAndRole(1L, UserRole.TEACHER) } returns null

        // when & then
        assertThrows<UnauthorizedException> {
            workbookService.createWorkbook(request)
        }

        verify(exactly = 1) { userQueryRepository.findByIdAndRole(1L, UserRole.TEACHER) }
        verify(exactly = 0) {
            problemRepository.findAllById(any())
            workbookRepository.save(any())
            workbookProblemRepository.saveAll<WorkbookProblem>(any())
        }
    }

    @Test
    fun `학습지 할당 - 성공`() {
        // given
        val workbookId = 1L
        val teacherId = 1L
        val request = AssignWorkbookRequest(
            studentIds = listOf(2L, 3L)
        )

        val workbook = createWorkbook(workbookId, "테스트 학습지", teacherId)
        val students = listOf(
            createUser(2L, "학생1", UserRole.STUDENT),
            createUser(3L, "학생2", UserRole.STUDENT)
        )

        every { workbookRepository.findById(workbookId) } returns Optional.of(workbook)
        every { userQueryRepository.findAllByIdAndRole(request.studentIds, UserRole.STUDENT) } returns students
        every {
            studentWorkbookRepository.saveAll<StudentWorkbook>(any())
        } returns listOf()

        // when & then
        assertDoesNotThrow {
            workbookService.assignWorkbook(workbookId, teacherId, request)
        }

        verify(exactly = 1) {
            workbookRepository.findById(workbookId)
            userQueryRepository.findAllByIdAndRole(request.studentIds, UserRole.STUDENT)
            studentWorkbookRepository.saveAll<StudentWorkbook>(any())
        }
    }

    @Test
    fun `학습지 할당 - 존재하지 않는 학습지인 경우 실패`() {
        // given
        val workbookId = 1L
        val teacherId = 1L
        val request = AssignWorkbookRequest(
            studentIds = listOf(2L, 3L)
        )

        every { workbookRepository.findById(workbookId) } returns Optional.empty()

        // when & then
        assertThrows<WorkbookNotFoundException> {
            workbookService.assignWorkbook(workbookId, teacherId, request)
        }

        verify(exactly = 1) { workbookRepository.findById(workbookId) }
        verify(exactly = 0) {
            userQueryRepository.findAllByIdAndRole(any(), any())
            studentWorkbookRepository.saveAll<StudentWorkbook>(any())
        }
    }

    @Test
    fun `학습지 할당 - 권한이 없는 경우 실패`() {
        // given
        val workbookId = 1L
        val teacherId = 2L  // 다른 교사
        val request = AssignWorkbookRequest(
            studentIds = listOf(2L, 3L)
        )

        val workbook = createWorkbook(workbookId, "테스트 학습지", 1L)

        every { workbookRepository.findById(workbookId) } returns Optional.of(workbook)

        // when & then
        assertThrows<UnauthorizedException> {
            workbookService.assignWorkbook(workbookId, teacherId, request)
        }

        verify(exactly = 1) { workbookRepository.findById(workbookId) }
        verify(exactly = 0) {
            userQueryRepository.findAllByIdAndRole(any(), any())
            studentWorkbookRepository.saveAll<StudentWorkbook>(any())
        }
    }

    private fun createUser(
        id: Long,
        name: String,
        role: UserRole
    ) = User(
        name = name,
        role = role
    ).apply {
        ReflectionTestUtils.setField(this, "id", id)
    }

    private fun createProblem(
        id: Long
    ) = Problem(
        content = "문제$id",
        answer = "답$id",
        unitCode = "UNIT001",
        level = 1,
        problemType = ProblemType.SELECTION
    ).apply {
        ReflectionTestUtils.setField(this, "id", id)
    }

    private fun createWorkbook(
        id: Long,
        name: String,
        teacherId: Long
    ) = Workbook(
        name = name,
        teacherId = teacherId
    ).apply {
        ReflectionTestUtils.setField(this, "id", id)
    }
}