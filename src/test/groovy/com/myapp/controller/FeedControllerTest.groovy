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

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FeedController)
class FeedControllerTest extends BaseControllerTest2 {

    @TestConfiguration
    static class Config {
        private DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        MicropostService micropostService() {
            factory.Stub(MicropostService, name: "micropostService")
        }
    }

    @Autowired
    MicropostService micropostService

    def "can show feed when signed in"() {
        given:
        User user = new User(id: 1, username: "test1@test.com", password: "secret", name: "test")
        signIn(user)

        def feed = [PostDTO.newInstance(new Micropost(id: 1, user: user, content: "content1"), true)]
        micropostService.findAsFeed(_ as PageParams) >> feed

        when:
        def response = perform(get("/api/feed"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("content1")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
                .andExpect(jsonPath('$[0].createdAt', nullValue()))
                .andExpect(jsonPath('$[0].user.email', is("test1@test.com")))
                .andExpect(jsonPath('$[0].user.avatarHash', is("94fba03762323f286d7c3ca9e001c541")))
    }

    def "can not show feed when not signed in"() {
        when:
        def response = mockMvc.perform(get("/api/feed"))

        then:
        response.andExpect(status().is(403))
    }

}
