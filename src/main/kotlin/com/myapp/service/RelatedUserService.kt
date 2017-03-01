package com.myapp.service

import com.myapp.domain.RelatedUser
import com.myapp.dto.request.PageParams

interface RelatedUserService {
    fun findFollowings(userId: Long, pageParams: PageParams): List<RelatedUser>
    fun findFollowers(userId: Long, pageParams: PageParams): List<RelatedUser>
}