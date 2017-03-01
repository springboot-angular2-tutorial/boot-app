package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.User
import com.myapp.dto.page.Page
import com.myapp.dto.request.UserEditParams
import com.myapp.dto.request.UserNewParams
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val relationshipRepository: RelationshipRepository,
    override val securityContextService: SecurityContextService
) : UserService, WithCurrentUser {

    override fun findOne(id: Long): User {
        val currentUser = currentUser()
        val user = userRepository.findOneWithStats(id)
        val isFollowedByMe = currentUser?.let {
            relationshipRepository.findOneByFollowerAndFollowed(it.id, user.id) != null
        }
        val isMyself = currentUser?.let { it.id == user.id }

        return user.copy(
            isFollowedByMe = isFollowedByMe,
            isMyself = isMyself
        )
    }

    override fun findMe(): User {
        val currentUser = currentUserOrThrow()
        return findOne(currentUser.id)
    }

    override fun findAll(page: Int, size: Int): Page<User> =
        userRepository.findAll(page, size)

    override fun create(params: UserNewParams): User {
        return userRepository.create(User(
            username = params.email,
            password = encrypt(params.password),
            name = params.name
        ))
    }

    override fun updateMe(params: UserEditParams) {
        val currentUser = currentUserOrThrow()
        userRepository.update(currentUser.copy(
            username = params.email ?: currentUser.username,
            password = params.password?.let { encrypt(it) } ?: currentUser.password,
            name = params.name ?: currentUser.name
        ))
    }

    private fun encrypt(secret: String) = BCryptPasswordEncoder().encode(secret)
}