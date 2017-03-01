package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.generated.tables.Relationship.RELATIONSHIP
import com.myapp.generated.tables.User.USER
import com.myapp.repository.exception.RelationshipDuplicatedException
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository
import com.myapp.generated.tables.User as UserTable

@Repository
class RelationshipRepositoryImpl(
    private val dsl: DSLContext
) : RelationshipRepository {

    private val FOLLOWER: UserTable = USER.`as`("follower")
    private val FOLLOWED: UserTable = USER.`as`("followed")

    override fun findOneByFollowerAndFollowed(followerId: Long, followedId: Long): Relationship? {
        return dsl.select()
            .from(RELATIONSHIP)
            .join(FOLLOWER)
            .on(RELATIONSHIP.FOLLOWER_ID.eq(FOLLOWER.ID))
            .join(FOLLOWED)
            .on(RELATIONSHIP.FOLLOWED_ID.eq(FOLLOWED.ID))
            .where(RELATIONSHIP.FOLLOWER_ID.eq(followerId))
            .and(RELATIONSHIP.FOLLOWED_ID.eq(followedId))
            .fetchOne(mapper())
    }

    override fun findAllByFollowerAndFollowedUsers(followerId: Long, userIds: List<Long>): List<Relationship> {
        return dsl.select()
            .from(RELATIONSHIP)
            .join(FOLLOWER)
            .on(RELATIONSHIP.FOLLOWER_ID.eq(FOLLOWER.ID))
            .join(FOLLOWED)
            .on(RELATIONSHIP.FOLLOWED_ID.eq(FOLLOWED.ID))
            .where(RELATIONSHIP.FOLLOWER_ID.eq(followerId))
            .and(RELATIONSHIP.FOLLOWED_ID.`in`(userIds))
            .orderBy(RELATIONSHIP.ID.asc())
            .fetch(mapper())
    }

    override fun create(relationship: Relationship): Relationship {
        try {
            return dsl.insertInto(RELATIONSHIP, RELATIONSHIP.FOLLOWED_ID, RELATIONSHIP.FOLLOWER_ID)
                .values(relationship.followed.id, relationship.follower.id)
                .returning()
                .fetchOne()
                .let(mapper(relationship))
        } catch(e: DataIntegrityViolationException) {
            throw RelationshipDuplicatedException("")
        }
    }

    override fun delete(id: Long) {
        dsl.deleteFrom(RELATIONSHIP)
            .where(RELATIONSHIP.ID.eq(id))
            .execute()
    }

    private fun mapper() = { r: Record ->
        Relationship(
            _id = r.into(RELATIONSHIP).id,
            followed = User(r.into(FOLLOWED)),
            follower = User(r.into(FOLLOWER))
        )
    }

    private fun mapper(original: Relationship) = { r: Record ->
        Relationship(
            _id = r.into(RELATIONSHIP).id,
            followed = original.followed,
            follower = original.follower
        )
    }

}