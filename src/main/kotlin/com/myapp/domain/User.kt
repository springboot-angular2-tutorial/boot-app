package com.myapp.domain

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.myapp.generated.tables.records.UserRecord
import com.myapp.md5
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class User(

    // ------- DB fields -------

    override val _id: Long? = null,

    @get:JsonIgnore
    @get:Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")
    @get:Size(min = 4, max = 30)
    val username: String,

    @get:JsonIgnore
    val password: String,

    @get:Size(min = 4, max = 30)
    val name: String,

    // ------- Others -------

    val userStats: UserStats? = null,

    @get:JsonProperty("isFollowedByMe")
    val isFollowedByMe: Boolean? = null, // null means unknown

    @get:JsonIgnore
    val isMyself: Boolean? = null // null means unknown

) : HasIdentity<Long> {

    constructor(record: UserRecord) : this(
        _id = record.id,
        name = record.name,
        username = record.username,
        password = record.password
    )

    @JsonGetter
    fun email(): String? {
        return isMyself?.let { if (it) username else null }
    }

    @JsonGetter
    fun avatarHash(): String = username.md5()

}
