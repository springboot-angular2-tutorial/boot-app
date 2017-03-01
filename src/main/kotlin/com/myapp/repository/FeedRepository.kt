package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams

interface FeedRepository {
    fun findFeed(userId: Long, pageParams: PageParams): List<Micropost>
}