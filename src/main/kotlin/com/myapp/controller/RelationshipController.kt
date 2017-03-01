package com.myapp.controller

import com.myapp.repository.exception.RelationshipDuplicatedException
import com.myapp.service.RelationshipService
import com.myapp.service.exception.RelationshipNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/relationships")
class RelationshipController(
    private val relationshipService: RelationshipService
) {

    @PostMapping(value = "/to/{followedId}")
    fun follow(@PathVariable("followedId") followedId: Long) {
        relationshipService.follow(followedId)
    }

    @DeleteMapping(value = "/to/{followedId}")
    fun unfollow(@PathVariable("followedId") followedId: Long) {
        relationshipService.unfollow(followedId)
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "already followed")
    @ExceptionHandler(RelationshipDuplicatedException::class)
    fun handleRelationshipDuplicated() {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(RelationshipNotFoundException::class)
    fun handleRelationshipNotFound() {
    }

}
