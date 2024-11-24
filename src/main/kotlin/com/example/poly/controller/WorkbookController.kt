package com.example.poly.controller

import com.example.poly.dto.request.AssignWorkbookRequest
import com.example.poly.dto.request.CreateWorkbookRequest
import com.example.poly.dto.request.SubmitAnswerRequest
import com.example.poly.dto.response.ProblemResponse
import com.example.poly.dto.response.SubmitAnswersResponse
import com.example.poly.dto.response.WorkbookAnalysisResponse
import com.example.poly.dto.response.WorkbookResponse
import com.example.poly.service.WorkbookService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/workbooks")
class WorkbookController(
    private val workbookService: WorkbookService
) {
    @PostMapping
    fun createWorkbook(
        @RequestBody @Valid request: CreateWorkbookRequest
    ): ResponseEntity<WorkbookResponse> {
        return ResponseEntity.ok(workbookService.createWorkbook(request))
    }

    @PostMapping("/{workbookId}/assign")
    fun assignWorkbook(
        @PathVariable workbookId: Long,
        @RequestParam teacherId: Long,
        @RequestBody @Valid request: AssignWorkbookRequest
    ): ResponseEntity<Unit> {
        workbookService.assignWorkbook(workbookId, teacherId, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{workbookId}/problems")
    fun getWorkbookProblems(
        @PathVariable workbookId: Long,
        @RequestParam studentId: Long
    ): ResponseEntity<List<ProblemResponse>> {
        return ResponseEntity.ok(
            workbookService.getWorkbookProblems(workbookId, studentId)
        )
    }

    @PutMapping("/{workbookId}/submit")
    fun submitAnswers(
        @PathVariable workbookId: Long,
        @RequestParam studentId: Long,
        @RequestBody @Valid answers: List<SubmitAnswerRequest>
    ): ResponseEntity<SubmitAnswersResponse> {
        return ResponseEntity.ok(
            workbookService.submitAnswers(workbookId, studentId, answers)
        )
    }

    @GetMapping("/{workbookId}/analyze")
    fun analyzeWorkbook(
        @PathVariable workbookId: Long,
        @RequestParam teacherId: Long
    ): ResponseEntity<WorkbookAnalysisResponse> {
        return ResponseEntity.ok(
            workbookService.analyzeWorkbook(workbookId, teacherId)
        )
    }
}