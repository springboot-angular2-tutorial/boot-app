package com.myapp.controller

import com.myapp.service.MicropostService
import com.myapp.service.exception.NotAuthorizedException
import com.myapp.testing.TestMicropost
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MicropostController::class)
class MicropostControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var micropostService: MicropostService

    @Test
    fun `create should create a post`() {
        signIn()
        `when`(micropostService.create("my content"))
            .doReturn(TestMicropost.copy(_id = 1, content = "my content"))

        val response = perform(post("/api/microposts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj("content" to "my content")
            })
        )

        verify(micropostService).create("my content")
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$.id", `is`(1)))
            andExpect(jsonPath("$.content", `is`("my content")))
        }
    }

    @Test
    fun `create should not create a post when not signed in`() {
        perform(post("/api/microposts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj("content" to "my content")
            })
        ).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `delete should delete a post`() {
        signIn()

        val response = perform(delete("/api/microposts/1"))

        verify(micropostService).delete(1)
        response.andExpect(status().isOk)
    }

    @Test
    fun `delete should not delete a post when not signed in`() {
        perform(delete("/api/microposts/1")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `delete should not delete a post which belongs to others`() {
        signIn()
        `when`(micropostService.delete(1)).doThrow(NotAuthorizedException(""))

        perform(delete("/api/microposts/1")).apply {
            andExpect(status().isForbidden)
        }
    }

}