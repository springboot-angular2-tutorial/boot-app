package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.dto.PageParams
import com.myapp.dto.PostDTO
import com.myapp.service.MicropostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory

import java.time.LocalDateTime

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FeedController)
class FeedControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        MicropostService micropostService(DetachedMockFactory f) {
            f.Stub(MicropostService, name: "micropostService")
        }
    }

    @Autowired
    MicropostService micropostService

    def "can show feed when signed in"() {
        given:
        User user = signIn()
        Date now = new Date()

        def feed = [PostDTO.newInstance(new Micropost(id: 1, user: user, content: "content1", createdAt: now), true)]
        micropostService.findAsFeed(_ as PageParams) >> feed

        when:
        def response = perform(get("/api/feed"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$', hasSize(1)))
            andExpect(jsonPath('$[0].content', is("content1")))
            andExpect(jsonPath('$[0].isMyPost', is(true)))
            andExpect(jsonPath('$[0].createdAt', greaterThanOrEqualTo(now.time)))
            andExpect(jsonPath('$[0].user.email', nullValue()))
            andExpect(jsonPath('$[0].user.avatarHash', is("b642b4217b34b1e8d3bd915fc65c4452")))
        }
    }

    def "can not show feed when not signed in"() {
        when:
        def response = perform(get("/api/feed"))

        then:
        response.andExpect(status().isUnauthorized())
    }

}
