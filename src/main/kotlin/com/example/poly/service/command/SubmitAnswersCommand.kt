package com.example.poly.service.command

data class SubmitAnswersCommand(
    val workbookId: Long,
    val studentId: Long,
    val answers: List<AnswerData>
) {
    data class AnswerData(
        val problemId: Long,
        val answer: String
    )

    init {
        require(answers.isNotEmpty()) { "Answers cannot be empty" }
    }
}