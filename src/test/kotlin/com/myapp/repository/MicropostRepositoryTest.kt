package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Micropost.MICROPOST
import com.myapp.generated.tables.User.USER
import com.myapp.repository.exception.RecordNotFoundException
import com.myapp.testing.TestUser
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith


class MicropostRepositoryTest : BaseRepositoryTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var micropostRepository: MicropostRepository

    @Test
    fun `findOne should find a post`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()
        dsl.insertInto(MICROPOST, MICROPOST.ID, MICROPOST.CONTENT, MICROPOST.USER_ID)
            .values(101, "my content", 1)
            .execute()

        val post = micropostRepository.findOne(101)

        assertThat(post).isNotNull()
        assertThat(post.content).isEqualTo("my content")
        assertThat(post.user.id).isEqualTo(1)
    }

    @Test
    fun `findOne should throw when no record`() {
        assertFailsWith<RecordNotFoundException> {
            micropostRepository.findOne(1)
        }
    }

    @Test
    fun `findAllByUser should find posts by user`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "user1")
            .values(2, "test2@test.com", "secret123", "user2")
            .execute()
        dsl.insertInto(MICROPOST, MICROPOST.ID, MICROPOST.CONTENT, MICROPOST.USER_ID)
            .values(101, "my content 1", 1)
            .values(102, "my content 2", 1)
            .values(103, "user2 content 1", 2)
            .execute()

        val posts = micropostRepository.findAllByUser(1, PageParams())

        assertThat(posts.size).isEqualTo(2)
        assertThat(posts.first().content).isEqualTo("my content 2")
        assertThat(posts.last().content).isEqualTo("my content 1")
        assertThat(posts.first().user.id).isEqualTo(1)
    }

    @Test
    fun `findAllByUser should find posts with page params`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "user1")
            .execute()
        dsl.insertInto(MICROPOST, MICROPOST.ID, MICROPOST.CONTENT, MICROPOST.USER_ID)
            .values(101, "my content 1", 1)
            .values(102, "my content 2", 1)
            .values(103, "my content 3", 1)
            .execute()

        micropostRepository.findAllByUser(1, PageParams(sinceId = 102)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().content).isEqualTo("my content 3")
        }
        micropostRepository.findAllByUser(1, PageParams(maxId = 102)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().content).isEqualTo("my content 1")
        }
        micropostRepository.findAllByUser(1, PageParams(count = 2)).apply {
            assertThat(size).isEqualTo(2)
        }
    }

    @Test
    fun `create should create a post`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()

        val createdPost = micropostRepository.create(Micropost(
            content = "my content",
            user = TestUser.copy(_id = 1)
        ))
        val postRecord = dsl.fetchOne(MICROPOST)
        val count = dsl.fetchCount(MICROPOST)

        assertThat(createdPost).isNotNull()
        assertThat(createdPost.id).isEqualTo(postRecord.id)
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `delete should delete a post`() {
        dsl.insertInto(USER, USER.ID, USER.USERNAME, USER.PASSWORD, USER.NAME)
            .values(1, "test1@test.com", "secret123", "test1")
            .execute()
        dsl.insertInto(MICROPOST, MICROPOST.ID, MICROPOST.CONTENT, MICROPOST.USER_ID)
            .values(101, "my content", 1)
            .execute()

        micropostRepository.delete(101)
        val count = dsl.fetchCount(MICROPOST)

        assertThat(count).isEqualTo(0)
    }

}

