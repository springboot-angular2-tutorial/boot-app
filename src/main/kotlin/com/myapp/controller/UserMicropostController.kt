package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.service.MicropostService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserMicropostController(
    private val micropostService: MicropostService
) {

    @GetMapping(path = arrayOf("/{userId:\\d+}/microposts"))
    fun list(
        @PathVariable("userId") userId: Long,
        @RequestParam(required = false) sinceId: Long?,
        @RequestParam(required = false) maxId: Long?,
        @RequestParam(required = false, defaultValue = "20") count: Int
    ): List<Micropost> {
        val pageParams = PageParams(sinceId, maxId, count)
        return micropostService.findAllByUser(userId, pageParams)
    }

    @GetMapping(path = arrayOf("/me/microposts"))
    fun listMyPosts(
        @RequestParam(required = false) sinceId: Long?,
        @RequestParam(required = false) maxId: Long?,
        @RequestParam(required = false, defaultValue = "20") count: Int
    ): List<Micropost> {
        val pageParams = PageParams(sinceId, maxId, count)
        return micropostService.findMyPosts(pageParams)
    }

}
