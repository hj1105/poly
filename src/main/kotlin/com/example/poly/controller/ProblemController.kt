package com.example.poly.controller

import com.example.poly.dto.request.ProblemSearchRequest
import com.example.poly.dto.response.ProblemResponse
import com.example.poly.service.ProblemService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("problems")
class ProblemController(
    private val problemService: ProblemService
) {
    @GetMapping
    fun searchProblems(
        @Valid request: ProblemSearchRequest
    ): ResponseEntity<List<ProblemResponse>> {
        return ResponseEntity.ok(
            problemService.findProblems(
                totalCount = request.totalCount,
                unitCodes = request.unitCodeList,
                level = request.level,
                problemType = request.problemType
            )
        )
    }
}