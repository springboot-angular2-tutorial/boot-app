package com.myapp.repository

import com.myapp.domain.User
import com.myapp.generated.tables.User.USER
import com.myapp.repository.exception.EmailDuplicatedException
import com.myapp.repository.exception.RecordInvalidException
import com.myapp.repository.exception.RecordNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith


class UserRepositoryTest : BaseRepositoryTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `findOne should find a user`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()

        val user = userRepository.findOne(1)

        assertThat(user).isNotNull()
        assertThat(user.username).isEqualTo("test1@test.com")
    }

    @Test
    fun `findOne should throw when no record`() {
        assertFailsWith<RecordNotFoundException> {
            userRepository.findOne(1)
        }
    }

    @Test
    fun `findOneWithStats should find a user with stats`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .returning(USER.ID)
            .fetchOne()

        val user = userRepository.findOneWithStats(1)

        assertThat(user).isNotNull()
        assertThat(user.userStats).isNotNull()
        assertThat(user.userStats?.micropostCnt).isEqualTo(0)
    }

    @Test
    fun `findOneWithStats should throw when no record`() {
        assertFailsWith<RecordNotFoundException> {
            userRepository.findOneWithStats(1)
        }
    }

    @Test
    fun `findOneByUsername should find user by name`() {
        dsl.insertInto(USER, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values("test1@test.com", "secret123", "test1")
            .execute()

        val user = userRepository.findOneByUsername("test1@test.com")

        assertThat(user).isNotNull()
        assertThat(user?.username).isEqualTo("test1@test.com")
    }

    @Test
    fun `findOneByUsername should be null when no matched user`() {
        val user = userRepository.findOneByUsername("test1@test.com")

        assertThat(user).isNull()
    }

    @Test
    fun `findAll should find users`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .values(2, "test2@test.com", "secret123", "test2")
            .execute()

        val page = userRepository.findAll(page = 1, size = 10)

        assertThat(page.content.size).isEqualTo(2)
        assertThat(page.content.first().username).isEqualTo("test1@test.com")
        assertThat(page.content.last().username).isEqualTo("test2@test.com")
        assertThat(page.totalPages).isEqualTo(1)
    }

    @Test
    fun `findAll should find users on page 2`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .values(2, "test2@test.com", "secret123", "test2")
            .execute()

        val page = userRepository.findAll(page = 2, size = 1)

        assertThat(page.content.size).isEqualTo(1)
        assertThat(page.content.first().username).isEqualTo("test2@test.com")
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    fun `create should create a user`() {
        val createdUser = userRepository.create(User(
            username = "test1@test.com",
            password = "secret123",
            name = "test1"
        ))
        val userRecord = dsl.fetchOne(USER)
        val count = dsl.fetchCount(USER)

        assertThat(createdUser).isNotNull()
        assertThat(createdUser.id).isEqualTo(userRecord.id)
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `create should not create a user when it is invalid`() {
        assertFailsWith<RecordInvalidException> {
            userRepository.create(User(
                username = "test1 AT test.com",
                password = "secret123",
                name = "test1"
            ))
        }
    }

    @Test
    fun `create should throw when email is duplicated`() {
        dsl.insertInto(USER, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values("test1@test.com", "secret123", "test1")
            .execute()

        assertFailsWith<EmailDuplicatedException> {
            userRepository.create(User(
                username = "test1@test.com",
                password = "secret123",
                name = "test1"
            ))
        }
    }

    @Test
    fun `update should update a user`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()

        userRepository.update(User(
            _id = 1,
            username = "test1@test.com",
            password = "secret123",
            name = "test2"
        ))
        val userRecord = dsl.fetchOne(USER)

        assertThat(userRecord.name).isEqualTo("test2")
    }

    @Test
    fun `update should not update a user when it is invalid`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()

        assertFailsWith<RecordInvalidException> {
            userRepository.update(User(
                _id = 1,
                username = "test1 AT test.com",
                password = "secret123",
                name = "test2"
            ))
        }
    }

    @Test
    fun `update should throw when email is duplicated`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .values(2, "test2@test.com", "secret123", "test2")
            .execute()

        assertFailsWith<EmailDuplicatedException> {
            userRepository.update(User(
                _id = 2,
                username = "test1@test.com",
                password = "secret123",
                name = "test2"
            ))
        }
    }

}

