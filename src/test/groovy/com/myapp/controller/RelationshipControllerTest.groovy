package com.myapp.controller

import com.myapp.service.RelationshipService
import com.myapp.service.exceptions.RelationshipNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RelationshipController)
class RelationshipControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        RelationshipService relationshipService(DetachedMockFactory f) {
            return f.Mock(RelationshipService)
        }
    }

    @Autowired
    RelationshipService relationshipService

    def "can follow another user"() {
        given:
        signIn()

        when:
        def response = perform(post("/api/relationships/to/1"))

        then:
        1 * relationshipService.follow(1)
        response.andExpect(status().isOk())
    }

    def "can not follow another user when not signed in"() {
        when:
        def response = perform(post("/api/relationships/to/1"))

        then:
        response.andExpect(status().isUnauthorized())
    }

    def "can unfollow another user"() {
        given:
        signIn()

        when:
        def response = perform(delete("/api/relationships/to/1"))

        then:
        1 * relationshipService.unfollow(1)
        response.andExpect(status().isOk())
    }

    def "can not unfollow another user when not signed in"() {
        when:
        def response = perform(delete("/api/relationships/to/1"))

        then:
        response.andExpect(status().isUnauthorized())
    }

    def "can not unfollow another user when have already followed"() {
        given:
        signIn()
        relationshipService.unfollow(1) >> {
            throw new RelationshipNotFoundException()
        }

        when:
        def response = perform(delete("/api/relationships/to/1"))

        then:
        response.andExpect(status().isNotFound())
    }

}
