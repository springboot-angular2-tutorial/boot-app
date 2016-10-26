package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.service.MicropostService
import com.myapp.service.exceptions.NotPermittedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import spock.mock.DetachedMockFactory

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MicropostController)
class MicropostControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        MicropostService micropostService(DetachedMockFactory f) {
            f.Mock(MicropostService, name: "micropostService")
        }
    }

    @Autowired
    MicropostService micropostService

    def "can create a micropost"() {
        given:
        signIn()
        String content = "my content"

        when:
        def response = perform(post("/api/microposts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content: content))
        )

        then:
        micropostService.saveMyPost(_ as Micropost) >> new Micropost(content)
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$.content').exists())
            andExpect(jsonPath('$.content', is(content)))
        }
    }

    def "can not create a micropost when not signed in"() {
        when:
        def response = perform(post("/api/microposts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content: "test"))
        )

        then:
        response.andExpect(status().isUnauthorized())
    }

    def "can delete a micropost"() {
        given:
        signIn()

        when:
        def response = perform(delete("/api/microposts/1"))

        then:
        1 * micropostService.delete(1)
        response.andExpect(status().isOk())
    }

    def "can not delete a micropost when not signed in"() {
        when:
        def response = perform(delete("/api/microposts/1"))

        then:
        response.andExpect(status().isUnauthorized())
    }

    def "can not delete a micropost when have no permission"() {
        given:
        signIn()
        micropostService.delete(1) >> { throw new NotPermittedException("") }

        when:
        def response = perform(delete("/api/microposts/1"))

        then:
        response.andExpect(status().isForbidden())
    }

}
