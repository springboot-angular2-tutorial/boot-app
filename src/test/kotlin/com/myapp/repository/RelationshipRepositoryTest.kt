package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.generated.tables.Relationship.RELATIONSHIP
import com.myapp.generated.tables.User.USER
import com.myapp.repository.exception.RelationshipDuplicatedException
import com.myapp.testing.TestUser
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith

class RelationshipRepositoryTest : BaseRepositoryTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Autowired
    lateinit var relationshipRepository: RelationshipRepository

    @Test
    fun `findOneByFollowerAndFollowed should find a relationship by follower and followed`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // follower
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(101, 1, 2)
                .execute()
        }

        val relationship = relationshipRepository.findOneByFollowerAndFollowed(1, 2)

        assertThat(relationship).isNotNull()
        assertThat(relationship?.id).isEqualTo(101)
        assertThat(relationship?.follower?.id).isEqualTo(1)
        assertThat(relationship?.followed?.id).isEqualTo(2)
    }

    @Test
    fun `findOneByFollowerAndFollowed should be null when no matched relationship`() {
        val relationship = relationshipRepository.findOneByFollowerAndFollowed(1, 2)

        assertThat(relationship).isNull()
    }

    @Test
    fun `findAllByFollowerAndFollowedUsers should find relationships by follower and followed users`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // follower
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .values(3, "test3@test.com", "secret123", "user 3") // followed
                .values(4, "test4@test.com", "secret123", "user 4") // followed
                .values(5, "test5@test.com", "secret123", "user 5") // others
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(101, 1, 2)
                .values(102, 1, 3)
                .execute()
        }

        val relationships = relationshipRepository
            .findAllByFollowerAndFollowedUsers(1, listOf(2, 3, 5))

        assertThat(relationships.size).isEqualTo(2)
        assertThat(relationships.first().id).isEqualTo(101)
        assertThat(relationships.first().follower.id).isEqualTo(1)
        assertThat(relationships.first().followed.id).isEqualTo(2)
        assertThat(relationships.last().id).isEqualTo(102)
        assertThat(relationships.last().follower.id).isEqualTo(1)
        assertThat(relationships.last().followed.id).isEqualTo(3)
    }

    @Test
    fun `create should create a relationship`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // follower
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .execute()
        }

        val createdRelationship = relationshipRepository.create(Relationship(
            follower = TestUser.copy(_id = 1),
            followed = TestUser.copy(_id = 2)
        ))
        val relationshipRecord = dsl.fetchOne(RELATIONSHIP)
        val count = dsl.fetchCount(RELATIONSHIP)

        assertThat(createdRelationship).isNotNull()
        assertThat(createdRelationship.id).isEqualTo(relationshipRecord.id)
        assertThat(count).isEqualTo(1)
    }


    @Test
    fun `create should throw when relationship is duplicated`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // follower
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(101, 1, 2)
                .execute()
        }

        assertFailsWith<RelationshipDuplicatedException> {
            relationshipRepository.create(Relationship(
                follower = TestUser.copy(_id = 1),
                followed = TestUser.copy(_id = 2)
            ))
        }
    }

    @Test
    fun `delete should delete a relationship`() {
        with(USER) {
            dsl.insertInto(this, ID, USERNAME, PASSWORD, NAME)
                .values(1, "test1@test.com", "secret123", "user 1") // follower
                .values(2, "test2@test.com", "secret123", "user 2") // followed
                .execute()
        }
        with(RELATIONSHIP) {
            dsl.insertInto(this, ID, FOLLOWER_ID, FOLLOWED_ID)
                .values(101, 1, 2)
                .execute()
        }

        relationshipRepository.delete(101)
        val count = dsl.fetchCount(RELATIONSHIP)

        assertThat(count).isEqualTo(0)
    }

}