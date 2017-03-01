package com.myapp.domain

data class Relationship(
    override val _id: Long? = null,
    val follower: User,
    val followed: User
) : HasIdentity<Long>
