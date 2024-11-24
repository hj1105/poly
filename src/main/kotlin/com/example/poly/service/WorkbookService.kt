package com.example.poly.service

import com.example.poly.common.InvalidRequestException
import com.example.poly.common.ProblemNotFoundException
import com.example.poly.common.UnauthorizedException
import com.example.poly.common.WorkbookNotFoundException
import com.example.poly.common.enums.UserRole
import com.example.poly.dto.request.AssignWorkbookRequest
import com.example.poly.dto.request.CreateWorkbookRequest
import com.example.poly.dto.request.SubmitAnswerRequest
import com.example.poly.dto.response.ProblemResponse
import com.example.poly.dto.response.SubmitAnswersResponse
import com.example.poly.dto.response.WorkbookAnalysisResponse
import com.example.poly.dto.response.WorkbookResponse
import com.example.poly.repository.*
import com.example.poly.repository.entity.ProblemAnswer
import com.example.poly.repository.entity.StudentWorkbook
import com.example.poly.repository.entity.Workbook
import com.example.poly.repository.entity.WorkbookProblem
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val workbookProblemRepository: WorkbookProblemRepository,
    private val workbookProblemQueryRepository: WorkbookProblemQueryRepository,
    private val studentWorkbookRepository: StudentWorkbookRepository,
    private val studentWorkbookQueryRepository: StudentWorkbookQueryRepository,
    private val problemAnswerRepository: ProblemAnswerRepository,
    private val problemAnswerQueryRepository: ProblemAnswerQueryRepository,
    private val problemRepository: ProblemRepository,
    private val userQueryRepository: UserQueryRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createWorkbook(request: CreateWorkbookRequest): WorkbookResponse {
        val teacher = userQueryRepository.findByIdAndRole(request.teacherId, UserRole.TEACHER)
            ?: throw UnauthorizedException("Only teachers can create workbooks")

        require(request.problemIds.size <= 50) { "Maximum 50 problems allowed" }

        // 문제 존재 여부 확인
        val problems = problemRepository.findAllById(request.problemIds)
        if (problems.size != request.problemIds.size) {
            val notFoundProblemIds = request.problemIds - problems.map { it.id!! }.toSet()
            throw InvalidRequestException("Problems not found with ids: $notFoundProblemIds")
        }

        val workbook = workbookRepository.save(
            Workbook(
                name = request.name,
                teacherId = teacher.id!!
            )
        )

        val workbookProblems = request.problemIds.mapIndexed { index, problemId ->
            WorkbookProblem(
                workbookId = workbook.id!!,
                problemId = problemId,
                orderNumber = index + 1
            )
        }

        workbookProblemRepository.saveAll(workbookProblems)

        return WorkbookResponse.from(workbook, workbookProblems.size)
    }

    @Transactional
    fun assignWorkbook(
        workbookId: Long,
        teacherId: Long,
        request: AssignWorkbookRequest
    ) {
        val workbook = workbookRepository.findById(workbookId)
            .orElseThrow { WorkbookNotFoundException(workbookId) }

        if (workbook.teacherId != teacherId) {
            throw UnauthorizedException("Only the creator can assign this workbook")
        }

        val students = userQueryRepository.findAllByIdAndRole(request.studentIds, UserRole.STUDENT)
        if (students.size != request.studentIds.size) {
            val notFoundStudentIds = request.studentIds - students.map { it.id!! }.toSet()
            throw InvalidRequestException("Students not found with ids: $notFoundStudentIds")
        }

        val studentWorkbooks = request.studentIds.map { studentId ->
            StudentWorkbook(
                studentId = studentId,
                workbookId = workbookId
            )
        }

        try {
            studentWorkbookRepository.saveAll(studentWorkbooks)
        } catch (e: DataIntegrityViolationException) {
            // 중복 할당은 무시
            logger.info("Some students already have this workbook assigned")
        }
    }

    fun getWorkbookProblems(workbookId: Long, studentId: Long): List<ProblemResponse> {
        val studentWorkbook = studentWorkbookQueryRepository
            .findByStudentIdAndWorkbookId(studentId, workbookId)
            ?: throw InvalidRequestException("Workbook is not assigned to this student")

        return workbookProblemQueryRepository.findActiveProblemsInWorkbook(workbookId)
            .map { ProblemResponse.from(it) }
    }

    @Transactional
    fun submitAnswers(
        workbookId: Long,
        studentId: Long,
        request: List<SubmitAnswerRequest>
    ): SubmitAnswersResponse {
        val studentWorkbook = studentWorkbookQueryRepository
            .findByStudentIdAndWorkbookId(studentId, workbookId)
            ?: throw InvalidRequestException("Workbook is not assigned to this student")

        val problems = problemRepository.findAllById(request.map { it.problemId })
            .associateBy { it.id!! }

        val problemAnswers = request.map { answer ->
            val problem = problems[answer.problemId]
                ?: throw ProblemNotFoundException(answer.problemId)

            ProblemAnswer(
                studentWorkbookId = studentWorkbook.id!!,
                problemId = answer.problemId,
                studentAnswer = answer.answer,
                isCorrect = answer.answer == problem.answer
            )
        }

        problemAnswerRepository.saveAll(problemAnswers)

        return SubmitAnswersResponse(
            correctCount = problemAnswers.count { it.isCorrect },
            totalCount = problemAnswers.size,
            correctRate = problemAnswers.count { it.isCorrect }.toDouble() / problemAnswers.size
        )
    }

    fun analyzeWorkbook(workbookId: Long, teacherId: Long): WorkbookAnalysisResponse {
        val workbook = workbookRepository.findById(workbookId)
            .orElseThrow { WorkbookNotFoundException(workbookId) }

        if (workbook.teacherId != teacherId) {
            throw UnauthorizedException("Only the creator can analyze this workbook")
        }

        val studentWorkbooks = studentWorkbookQueryRepository.findAllByWorkbookId(workbookId)
        val problemAnswers = problemAnswerQueryRepository
            .findAllByStudentWorkbookIds(studentWorkbooks.map { it.id!! })

        val studentStats = calculateStudentStats(studentWorkbooks, problemAnswers)
        val problemStats = calculateProblemStats(workbookId, problemAnswers)

        return WorkbookAnalysisResponse(
            workbookId = workbookId,
            workbookName = workbook.name,
            teacherId = teacherId,
            studentsCount = studentWorkbooks.size,
            studentStats = studentStats,
            problemStats = problemStats
        )
    }

    private fun calculateStudentStats(
        studentWorkbooks: List<StudentWorkbook>,
        problemAnswers: List<ProblemAnswer>
    ): List<WorkbookAnalysisResponse.StudentStat> {
        return studentWorkbooks.map { studentWorkbook ->
            val studentAnswers = problemAnswers.filter {
                it.studentWorkbookId == studentWorkbook.id
            }

            val student = userRepository.findById(studentWorkbook.studentId).get()

            WorkbookAnalysisResponse.StudentStat(
                studentId = student.id!!,
                studentName = student.name,
                correctCount = studentAnswers.count { it.isCorrect },
                totalCount = studentAnswers.size,
                correctRate = if (studentAnswers.isEmpty()) 0.0
                else studentAnswers.count { it.isCorrect }.toDouble() / studentAnswers.size
            )
        }
    }

    private fun calculateProblemStats(
        workbookId: Long,
        problemAnswers: List<ProblemAnswer>
    ): List<WorkbookAnalysisResponse.ProblemStat> {
        val workbookProblems = workbookProblemRepository.findAllByWorkbookId(workbookId)

        return workbookProblems.map { workbookProblem ->
            val answers = problemAnswers.filter {
                it.problemId == workbookProblem.problemId
            }

            WorkbookAnalysisResponse.ProblemStat(
                problemId = workbookProblem.problemId,
                correctCount = answers.count { it.isCorrect },
                totalCount = answers.size,
                correctRate = if (answers.isEmpty()) 0.0
                else answers.count { it.isCorrect }.toDouble() / answers.size
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(WorkbookService::class.java)
    }
}