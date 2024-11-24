package com.example.poly.common

import org.springframework.http.HttpStatus

sealed class BusinessException(
    override val message: String,
    val errorCode: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)

class UserNotFoundException(userId: Long) : BusinessException(
    message = "User not found with id: $userId",
    errorCode = "USER_NOT_FOUND",
    status = HttpStatus.NOT_FOUND
)

class WorkbookNotFoundException(workbookId: Long) : BusinessException(
    message = "Workbook not found with id: $workbookId",
    errorCode = "WORKBOOK_NOT_FOUND",
    status = HttpStatus.NOT_FOUND
)

class ProblemNotFoundException(problemId: Long) : BusinessException(
    message = "Problem not found with id: $problemId",
    errorCode = "PROBLEM_NOT_FOUND",
    status = HttpStatus.NOT_FOUND
)

class UnauthorizedException(message: String) : BusinessException(
    message = message,
    errorCode = "UNAUTHORIZED",
    status = HttpStatus.UNAUTHORIZED
)

class InvalidRequestException(message: String) : BusinessException(
    message = message,
    errorCode = "INVALID_REQUEST",
    status = HttpStatus.BAD_REQUEST
)