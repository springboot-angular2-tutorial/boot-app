package com.myapp.repository

import com.myapp.domain.Relationship

interface RelationshipRepository {

    fun findOneByFollowerAndFollowed(followerId: Long, followedId: Long): Relationship?
    fun findAllByFollowerAndFollowedUsers(followerId: Long, userIds: List<Long>): List<Relationship>
    fun create(relationship: Relationship): Relationship
    fun delete(id: Long)

}