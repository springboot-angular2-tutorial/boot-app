package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.dto.request.PageParams
import com.myapp.generated.tables.Micropost.MICROPOST
import com.myapp.generated.tables.User.USER
import com.myapp.repository.exception.RecordNotFoundException
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class MicropostRepositoryImpl(
    private val dsl: DSLContext
) : MicropostRepository, Pager {

    override fun findOne(id: Long) =
        dsl.select()
            .from(MICROPOST)
            .join(USER)
            .on(MICROPOST.USER_ID.eq(USER.ID))
            .where(MICROPOST.ID.eq(id))
            .fetchOne(mapper()) ?: throw RecordNotFoundException()

    override fun findAllByUser(userId: Long, pageParams: PageParams): List<Micropost> {
        return dsl.select()
            .from(MICROPOST)
            .join(USER)
            .on(MICROPOST.USER_ID.eq(USER.ID))
            .where(MICROPOST.USER_ID.eq(userId))
            .and(pageParams.toCondition(MICROPOST.ID))
            .orderBy(MICROPOST.ID.desc())
            .limit(pageParams.count)
            .fetch(mapper())
    }

    override fun create(micropost: Micropost) =
        dsl.insertInto(MICROPOST, MICROPOST.CONTENT, MICROPOST.USER_ID)
            .values(micropost.content, micropost.user.id)
            .returning()
            .fetchOne()
            .let(mapper(micropost))

    override fun delete(id: Long) {
        dsl.deleteFrom(MICROPOST)
            .where(MICROPOST.ID.eq(id))
            .execute()
    }

    private fun mapper() = { r: Record ->
        Micropost(
            record = r.into(MICROPOST),
            user = User(r.into(USER))
        )
    }

    private fun mapper(original: Micropost) = { r: Record ->
        Micropost(
            record = r.into(MICROPOST),
            user = original.user
        )
    }

}