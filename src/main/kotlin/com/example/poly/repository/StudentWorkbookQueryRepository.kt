package com.example.poly.repository

import com.example.poly.repository.entity.QStudentWorkbook
import com.example.poly.repository.entity.StudentWorkbook
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class StudentWorkbookQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findByStudentIdAndWorkbookId(studentId: Long, workbookId: Long): StudentWorkbook? {
        return queryFactory
            .selectFrom(QStudentWorkbook.studentWorkbook)
            .where(
                QStudentWorkbook.studentWorkbook.studentId.eq(studentId),
                QStudentWorkbook.studentWorkbook.workbookId.eq(workbookId),
                QStudentWorkbook.studentWorkbook.deletedAt.isNull
            )
            .fetchOne()
    }

    fun findAllByWorkbookId(workbookId: Long): List<StudentWorkbook> {
        return queryFactory
            .selectFrom(QStudentWorkbook.studentWorkbook)
            .where(
                QStudentWorkbook.studentWorkbook.workbookId.eq(workbookId),
                QStudentWorkbook.studentWorkbook.deletedAt.isNull
            )
            .fetch()
    }
}