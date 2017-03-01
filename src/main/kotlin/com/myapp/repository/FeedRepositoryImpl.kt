package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Feed.FEED
import com.myapp.generated.tables.User.USER
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class FeedRepositoryImpl(
    private val dsl: DSLContext
) : FeedRepository, Pager {

    override fun findFeed(userId: Long, pageParams: PageParams): List<Micropost> =
        dsl.select()
            .from(FEED)
            .join(USER)
            .on(FEED.USER_ID.eq(USER.ID))
            .where(FEED.FEED_USER_ID.eq(userId))
            .and(pageParams.toCondition(FEED.ID))
            .orderBy(FEED.ID.desc())
            .limit(pageParams.count)
            .fetch(mapper())

    private fun mapper() = { r: Record ->
        Micropost(
            record = r.into(FEED),
            user = User(r.into(USER))
        )
    }

}

