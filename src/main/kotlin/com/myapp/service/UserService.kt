package com.myapp.service

import com.myapp.domain.User
import com.myapp.dto.page.Page
import com.myapp.dto.request.UserEditParams
import com.myapp.dto.request.UserNewParams

interface UserService {
    fun findAll(page: Int, size: Int = 20): Page<User>
    fun findOne(id: Long): User
    fun findMe(): User
    fun create(params: UserNewParams): User
    fun updateMe(params: UserEditParams)
}