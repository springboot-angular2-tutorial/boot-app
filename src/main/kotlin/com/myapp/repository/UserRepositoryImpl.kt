package com.myapp.repository

import com.myapp.domain.User
import com.myapp.domain.UserStats
import com.myapp.dto.page.Page
import com.myapp.dto.page.PageImpl
import com.myapp.generated.tables.User.USER
import com.myapp.generated.tables.UserStats.USER_STATS
import com.myapp.generated.tables.records.UserRecord
import com.myapp.repository.exception.EmailDuplicatedException
import com.myapp.repository.exception.RecordInvalidException
import com.myapp.repository.exception.RecordNotFoundException
import org.jooq.DSLContext
import org.jooq.Record
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository
import javax.validation.Validator

@Repository
class UserRepositoryImpl(
    private val dsl: DSLContext,
    private val validator: Validator
) : UserRepository {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(UserRepositoryImpl::class.java)

    override fun findOne(id: Long) =
        dsl.selectFrom(USER)
            .where(USER.ID.eq(id))
            .fetchOne(mapper()) ?: throw RecordNotFoundException()

    override fun findOneWithStats(id: Long) =
        dsl.select()
            .from(USER)
            .join(USER_STATS)
            .on(USER.ID.eq(USER_STATS.USER_ID))
            .where(USER.ID.eq(id))
            .fetchOne(mapperWithStats()) ?: throw RecordNotFoundException()

    override fun findOneByUsername(username: String): User? {
        return dsl.selectFrom(USER)
            .where(USER.USERNAME.eq(username))
            .fetchOne(mapper())
    }

    override fun findAll(page: Int, size: Int): Page<User> {
        val content = dsl.selectFrom(USER)
            .orderBy(USER.ID)
            .seek((page - 1) * size.toLong())
            .limit(size)
            .fetch(mapper())

        val totalPages: Int = dsl.selectCount()
            .from(USER)
            .fetchOne()
            .value1()
            .div(size.toDouble())
            .ceil()
            .toInt()

        return PageImpl(
            content = content,
            totalPages = totalPages
        )
    }

    override fun create(user: User): User {
        validate(user)

        try {
            return dsl.insertInto(USER, USER.USERNAME, USER.PASSWORD, USER.NAME)
                .values(user.username, user.password, user.name)
                .returning()
                .fetchOne()
                .let(mapper())
        } catch (e: DataIntegrityViolationException) {
            throw EmailDuplicatedException("")
        }
    }

    override fun update(user: User) {
        validate(user)

        try {
            dsl.update(USER)
                .set(USER.USERNAME, user.username)
                .set(USER.PASSWORD, user.password)
                .set(USER.NAME, user.name)
                .where(USER.ID.eq(user.id))
                .execute()
        } catch(e: DataIntegrityViolationException) {
            throw EmailDuplicatedException("")
        }
    }

    private fun validate(user: User) = validator.validate(user).apply {
        if (isNotEmpty()) throw RecordInvalidException(toString())
    }

    private fun mapper(): (UserRecord) -> User = ::User

    private fun mapperWithStats(): (Record) -> User = {
        mapper().invoke(it.into(USER)).copy(
            userStats = UserStats(it.into(USER_STATS))
        )
    }

    private fun Double.ceil() = Math.ceil(this)
}
