package com.example.poly.repository

import com.example.poly.common.enums.UserRole
import com.example.poly.repository.entity.QUser
import com.example.poly.repository.entity.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findByIdAndRole(id: Long, role: UserRole): User? {
        return queryFactory
            .selectFrom(QUser.user)
            .where(
                QUser.user.id.eq(id)
                    .and(QUser.user.role.eq(role))
                    .and(QUser.user.deletedAt.isNull)
            )
            .fetchOne()
    }

    fun findAllByIdAndRole(ids: List<Long>, role: UserRole): List<User> {
        return queryFactory
            .selectFrom(QUser.user)
            .where(
                QUser.user.id.`in`(ids)
                    .and(QUser.user.role.eq(role))
                    .and(QUser.user.deletedAt.isNull)
            )
            .fetch()
    }
}