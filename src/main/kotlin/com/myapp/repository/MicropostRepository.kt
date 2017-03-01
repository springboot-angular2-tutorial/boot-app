package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.dto.request.PageParams

interface MicropostRepository {
    fun create(micropost: Micropost): Micropost
    fun delete(id: Long)
    fun findOne(id: Long): Micropost
    fun findAllByUser(userId: Long, pageParams: PageParams): List<Micropost>
}