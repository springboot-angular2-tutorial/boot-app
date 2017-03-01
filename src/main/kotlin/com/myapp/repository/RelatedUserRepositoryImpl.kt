package com.myapp.repository

import com.myapp.domain.RelatedUser
import com.myapp.domain.UserStats
import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Relationship.RELATIONSHIP
import com.myapp.generated.tables.User.USER
import com.myapp.generated.tables.UserStats.USER_STATS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import com.myapp.generated.tables.User as UserTable

@Repository
class RelatedUserRepositoryImpl(
    private val dsl: DSLContext
) : RelatedUserRepository, Pager {

    override fun findFollowers(userId: Long, pageParams: PageParams): List<RelatedUser> {
        return dsl.select()
            .from(USER)
            .join(RELATIONSHIP)
            .on(USER.ID.eq(RELATIONSHIP.FOLLOWER_ID))
            .join(USER_STATS)
            .on(USER.ID.eq(USER_STATS.USER_ID))
            .where(RELATIONSHIP.FOLLOWED_ID.eq(userId))
            .and(pageParams.toCondition(RELATIONSHIP.ID))
            .orderBy(RELATIONSHIP.ID.desc())
            .limit(pageParams.count)
            .fetch(mapper())
    }

    override fun findFollowings(userId: Long, pageParams: PageParams): List<RelatedUser> {
        return dsl.select()
            .from(USER)
            .join(RELATIONSHIP)
            .on(USER.ID.eq(RELATIONSHIP.FOLLOWED_ID))
            .join(USER_STATS)
            .on(USER.ID.eq(USER_STATS.USER_ID))
            .where(RELATIONSHIP.FOLLOWER_ID.eq(userId))
            .and(pageParams.toCondition(RELATIONSHIP.ID))
            .orderBy(RELATIONSHIP.ID.desc())
            .limit(pageParams.count)
            .fetch(mapper())
    }

    private fun mapper() = { r: Record ->
        val userRecord = r.into(USER)
        val relationshipRecord = r.into(RELATIONSHIP)
        val userStatsRecord = r.into(USER_STATS)
        RelatedUser(
            _id = userRecord.id,
            username = userRecord.username,
            name = userRecord.name,
            relationshipId = relationshipRecord.id,
            userStats = UserStats(userStatsRecord)
        )
    }

}