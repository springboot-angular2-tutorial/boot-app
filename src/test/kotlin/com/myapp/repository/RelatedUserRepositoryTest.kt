package com.myapp.repository

import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Relationship.RELATIONSHIP
import com.myapp.generated.tables.User.USER
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RelatedUserRepositoryTest : BaseRepositoryTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var relatedUserRepository: RelatedUserRepository

    @Test
    fun `findFollowers should find followers`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // subject
                .values(2, "test2@test.com", "secret123", "user 2") // follower
                .values(3, "test3@test.com", "secret123", "user 3") // follower
                .values(4, "test4@test.com", "secret123", "user 4") // not follower
                .execute()
        }
        with(RELATIONSHIP) { dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID) .values(102, 2, 1)
                .values(103, 3, 1)
                .execute()
        }

        val followers = relatedUserRepository.findFollowers(1, PageParams())

        assertThat(followers.size).isEqualTo(2)
        assertThat(followers.first().name).isEqualTo("user 3")
        assertThat(followers.first().relationshipId).isEqualTo(103)
        assertThat(followers.last().relationshipId).isEqualTo(102)
    }

    @Test
    fun `findFollowers should find followers with page params`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1")
                .values(2, "test2@test.com", "secret123", "user 2")
                .values(3, "test3@test.com", "secret123", "user 3")
                .values(4, "test4@test.com", "secret123", "user 4")
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(102, 2, 1)
                .values(103, 3, 1)
                .values(104, 4, 1)
                .execute()
        }

        relatedUserRepository.findFollowers(1, PageParams(sinceId = 103)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().username).isEqualTo("test4@test.com")
        }
        relatedUserRepository.findFollowers(1, PageParams(maxId = 103)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().username).isEqualTo("test2@test.com")
        }
        relatedUserRepository.findFollowers(1, PageParams(count = 2)).apply {
            assertThat(size).isEqualTo(2)
        }
    }

    @Test
    fun `findFollowings should find followings`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // subject
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .values(3, "test3@test.com", "secret123", "user 3") // followed
                .values(4, "test4@test.com", "secret123", "user 4") // not followed
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(102, 1, 2)
                .values(103, 1, 3)
                .execute()
        }

        val followings = relatedUserRepository.findFollowings(1, PageParams())

        assertThat(followings.size).isEqualTo(2)
        assertThat(followings.first().name).isEqualTo("user 3")
        assertThat(followings.first().relationshipId).isEqualTo(103)
        assertThat(followings.last().relationshipId).isEqualTo(102)
    }

    @Test
    fun `findFollowings should find followings with page params`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1")
                .values(2, "test2@test.com", "secret123", "user 2")
                .values(3, "test3@test.com", "secret123", "user 3")
                .values(4, "test4@test.com", "secret123", "user 4")
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(102, 1, 2)
                .values(103, 1, 3)
                .values(104, 1, 4)
                .execute()
        }

        relatedUserRepository.findFollowings(1, PageParams(sinceId = 103)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().username).isEqualTo("test4@test.com")
        }
        relatedUserRepository.findFollowings(1, PageParams(maxId = 103)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().username).isEqualTo("test2@test.com")
        }
        relatedUserRepository.findFollowings(1, PageParams(count = 2)).apply {
            assertThat(size).isEqualTo(2)
        }
    }
}