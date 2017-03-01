package com.myapp.service

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams


interface MicropostService {
    fun create(content: String): Micropost
    fun delete(id: Long)
    fun findAllByUser(userId: Long, pageParams: PageParams): List<Micropost>
    fun findMyPosts(pageParams: PageParams): List<Micropost>
}