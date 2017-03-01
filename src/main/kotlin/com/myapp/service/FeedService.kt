package com.myapp.service

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams

interface FeedService {

    fun findFeed(pageParams: PageParams): List<Micropost>

}
