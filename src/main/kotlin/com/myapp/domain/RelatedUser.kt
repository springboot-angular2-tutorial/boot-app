package com.myapp.domain

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.myapp.md5

data class RelatedUser(

    override val _id: Long? = null,

    val relationshipId: Long,

    @get:JsonIgnore
    val username: String,

    val name: String,

    val userStats: UserStats? = null,

    @get:JsonProperty("isFollowedByMe")
    val isFollowedByMe: Boolean? = null // null means unknown

) : HasIdentity<Long> {

    @JsonGetter
    fun avatarHash(): String = username.md5()

}