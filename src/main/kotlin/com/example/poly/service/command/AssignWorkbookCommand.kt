package com.example.poly.service.command

data class AssignWorkbookCommand(
    val workbookId: Long,
    val teacherId: Long,
    val studentIds: List<Long>
) {
    init {
        require(studentIds.isNotEmpty()) { "Student ids cannot be empty" }
    }
}