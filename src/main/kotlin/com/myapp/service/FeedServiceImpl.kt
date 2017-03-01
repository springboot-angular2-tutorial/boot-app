package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.repository.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedServiceImpl(
    private val feedRepository: FeedRepository,
    override val securityContextService: SecurityContextService
) : FeedService, WithCurrentUser {

    override fun findFeed(pageParams: PageParams): List<Micropost> {
        val currentUser = currentUserOrThrow()

        return feedRepository.findFeed(currentUser.id, pageParams)
            .map { it.copy(isMyPost = it.user.id == currentUser.id) }
    }

}