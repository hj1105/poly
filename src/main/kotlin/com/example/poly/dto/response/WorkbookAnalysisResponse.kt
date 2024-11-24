package com.example.poly.dto.response

data class WorkbookAnalysisResponse(
    val workbookId: Long,
    val workbookName: String,
    val teacherId: Long,
    val studentsCount: Int,
    val studentStats: List<StudentStat>,
    val problemStats: List<ProblemStat>
) {
    data class StudentStat(
        val studentId: Long,
        val studentName: String,
        val correctCount: Int,
        val totalCount: Int,
        val correctRate: Double
    )

    data class ProblemStat(
        val problemId: Long,
        val correctCount: Int,
        val totalCount: Int,
        val correctRate: Double
    )
}