package com.myapp.controller

import com.myapp.repository.exception.RelationshipDuplicatedException
import com.myapp.service.RelationshipService
import com.myapp.service.exception.RelationshipNotFoundException
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RelationshipController::class)
class RelationshipControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var relationshipService: RelationshipService

    @Test
    fun `follow should follow a user`() {
        signIn()

        val response = perform(post("/api/relationships/to/1"))

        verify(relationshipService).follow(1)
        response.andExpect(status().isOk)
    }

    @Test
    fun `follow should not follow a user when not signed in`() {
        perform(post("/api/relationships/to/1")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `follow should not follow when already followed`() {
        signIn()
        `when`(relationshipService.follow(1))
            .doThrow(RelationshipDuplicatedException(""))

        perform(post("/api/relationships/to/1")).apply {
            andExpect(status().isBadRequest)
        }
    }

    @Test
    fun `unfollow should unfollow a user`() {
        signIn()

        val response = perform(delete("/api/relationships/to/1"))

        verify(relationshipService).unfollow(1)
        response.andExpect(status().isOk)
    }

    @Test
    fun `unfollow should not unfollow a user when not signed in`() {
        perform(delete("/api/relationships/to/1")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `unfollow should not unfollow when relationship not found`() {
        signIn()
        `when`(relationshipService.unfollow(1))
            .doThrow(RelationshipNotFoundException())

        perform(delete("/api/relationships/to/1")).apply {
            andExpect(status().isNotFound)
        }
    }

}