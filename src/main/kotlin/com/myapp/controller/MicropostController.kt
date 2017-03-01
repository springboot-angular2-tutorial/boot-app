package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.service.MicropostService
import com.myapp.service.exception.NotAuthorizedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/microposts")
class MicropostController(
    private val micropostService: MicropostService
) {

    @PostMapping
    fun create(@RequestBody params: MicropostParams): Micropost {
        return micropostService.create(params.content)
    }

    @DeleteMapping(value = "{id}")
    fun delete(@PathVariable("id") id: Long) {
        micropostService.delete(id)
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorized() = Unit

    data class MicropostParams(val content: String)

}


