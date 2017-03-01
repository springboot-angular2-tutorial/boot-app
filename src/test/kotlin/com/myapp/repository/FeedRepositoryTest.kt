package com.myapp.repository

import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Micropost.MICROPOST
import com.myapp.generated.tables.Relationship.RELATIONSHIP
import com.myapp.generated.tables.User.USER
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FeedRepositoryTest : BaseRepositoryTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var feedRepository: FeedRepository

    @Test
    fun `findFeed should find a feed`() {
        populateTestData()

        val feed = feedRepository.findFeed(1, PageParams())

        assertThat(feed.size).isEqualTo(3)
        assertThat(feed.first().content).isEqualTo("user 2 - content1")
        assertThat(feed.last().content).isEqualTo("user 1 - content1")
    }

    @Test
    fun `findFeed should find a feed with page params`() {
        populateTestData()

        feedRepository.findFeed(1, PageParams(sinceId = 2)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().content).isEqualTo("user 2 - content1")
        }
        feedRepository.findFeed(1, PageParams(maxId = 2)).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().content).isEqualTo("user 1 - content1")
        }
        feedRepository.findFeed(1, PageParams(count = 2)).apply {
            assertThat(size).isEqualTo(2)
        }
    }

    private fun populateTestData() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1")
                .values(2, "test2@test.com", "secret123", "user 2")
                .values(3, "test3@test.com", "secret123", "user 3")
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, FOLLOWER_ID, FOLLOWED_ID)
                .values(1, 2)
                .execute()
        }
        with(MICROPOST) {
            dsl.insertInto(this, ID, CONTENT, USER_ID)
                .values(1, "user 1 - content1", 1)
                .values(2, "user 1 - content2", 1)
                .values(3, "user 2 - content1", 2)
                .values(4, "user 3 - content1", 3) // It won't be included in a feed of user 1.
                .execute()
        }
    }

}