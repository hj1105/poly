package com.example.poly.service.command

data class CreateWorkbookCommand(
    val teacherId: Long,
    val name: String,
    val problemIds: List<Long>
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(problemIds.isNotEmpty()) { "Problem ids cannot be empty" }
        require(problemIds.size <= 50) { "Maximum 50 problems allowed" }
    }
}
