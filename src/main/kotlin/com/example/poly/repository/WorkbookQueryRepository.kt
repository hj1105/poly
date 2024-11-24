package com.example.poly.repository

import com.example.poly.repository.entity.QWorkbook
import com.example.poly.repository.entity.Workbook
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class WorkbookQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findActiveByTeacherId(teacherId: Long): List<Workbook> {
        return queryFactory
            .selectFrom(QWorkbook.workbook)
            .where(
                QWorkbook.workbook.teacherId.eq(teacherId),
                QWorkbook.workbook.deletedAt.isNull
            )
            .fetch()
    }
}