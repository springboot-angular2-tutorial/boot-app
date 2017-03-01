package com.myapp.repository

import com.myapp.domain.RelatedUser
import com.myapp.dto.request.PageParams

interface RelatedUserRepository {
    fun findFollowers(userId: Long, pageParams: PageParams): List<RelatedUser>
    fun findFollowings(userId: Long, pageParams: PageParams): List<RelatedUser>
}