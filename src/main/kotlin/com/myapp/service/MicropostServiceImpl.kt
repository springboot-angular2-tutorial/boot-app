package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.repository.MicropostRepository
import com.myapp.service.exception.NotAuthorizedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class MicropostServiceImpl(
    private val micropostRepository: MicropostRepository,
    override val securityContextService: SecurityContextService
) : MicropostService, WithCurrentUser {

    override fun findAllByUser(userId: Long, pageParams: PageParams): List<Micropost> {
        val currentUser = currentUser()
        val isMyPost = currentUser?.let { it.id == userId }

        return micropostRepository.findAllByUser(userId, pageParams)
            .map { it.copy(isMyPost = isMyPost) }
    }

    override fun findMyPosts(pageParams: PageParams): List<Micropost> {
        val currentUser = currentUserOrThrow()
        return findAllByUser(currentUser.id, pageParams)
    }

    override fun create(content: String) =
        micropostRepository.create(Micropost(
            content = content,
            user = currentUserOrThrow()
        ))

    override fun delete(id: Long) {
        val currentUser = currentUserOrThrow()
        val post = micropostRepository.findOne(id)

        if (post.user.id == currentUser.id)
            micropostRepository.delete(id)
        else
            throw NotAuthorizedException("You can not delete this post.")
    }

}