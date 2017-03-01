package com.myapp.controller

import com.myapp.domain.RelatedUser
import com.myapp.dto.request.PageParams
import com.myapp.service.RelatedUserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users/{userId}")
class RelatedUserController(
    private val relatedUserService: RelatedUserService
) {

    @GetMapping(path = arrayOf("/followings"))
    fun followings(
        @PathVariable("userId") userId: Long,
        @RequestParam(required = false) sinceId: Long?,
        @RequestParam(required = false) maxId: Long?,
        @RequestParam(required = false, defaultValue = "20") count: Int
    ): List<RelatedUser> {
        val pageParams = PageParams(sinceId, maxId, count)
        return relatedUserService.findFollowings(userId, pageParams)
    }

    @GetMapping(path = arrayOf("/followers"))
    fun followers(
        @PathVariable("userId") userId: Long,
        @RequestParam(required = false) sinceId: Long?,
        @RequestParam(required = false) maxId: Long?,
        @RequestParam(required = false, defaultValue = "20") count: Int
    ): List<RelatedUser> {
        val pageParams = PageParams(sinceId, maxId, count)
        return relatedUserService.findFollowers(userId, pageParams)
    }

}
