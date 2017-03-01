package com.myapp.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.myapp.generated.tables.records.FeedRecord
import com.myapp.generated.tables.records.MicropostRecord
import java.util.*

data class Micropost(

    override val _id: Long? = null,

    val content: String,

    val createdAt: Date = Date(),

    val user: User,

    @get:JsonProperty("isMyPost")
    val isMyPost: Boolean? = null // null means unknown

) : HasIdentity<Long> {

    constructor(record: MicropostRecord, user: User) : this(
        _id = record.id,
        content = record.content,
        createdAt = record.createdAt,
        user = user
    )

    constructor(record: FeedRecord, user: User) : this(
        _id = record.id,
        content = record.content,
        createdAt = record.createdAt,
        user = user
    )

}