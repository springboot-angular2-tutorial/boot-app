package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.service.FeedService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feed")
class FeedController(
    private val feedService: FeedService
) {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(FeedController::class.java)

    @GetMapping
    fun feed(
        @RequestParam(required = false) sinceId: Long?,
        @RequestParam(required = false) maxId: Long?,
        @RequestParam(required = false, defaultValue = "20") count: Int
    ): List<Micropost> {
        return feedService.findFeed(PageParams(sinceId, maxId, count))
    }

}