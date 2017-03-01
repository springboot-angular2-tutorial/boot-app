package com.myapp.repository

import com.myapp.domain.User
import com.myapp.dto.page.Page

interface UserRepository {
    fun findOne(id: Long): User
    fun findOneWithStats(id: Long): User
    fun findOneByUsername(username: String): User?
    fun findAll(page: Int, size: Int): Page<User>
    fun create(user: User): User
    fun update(user: User)
}