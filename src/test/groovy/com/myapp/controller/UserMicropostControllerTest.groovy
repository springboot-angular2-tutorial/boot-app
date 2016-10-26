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

@WebMvcTest(UserMicropostController)
class UserMicropostControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        MicropostService micropostService(DetachedMockFactory f) {
            return f.Mock(MicropostService)
        }
    }

    @Autowired
    MicropostService micropostService

    def "can list microposts"() {
        given:
        User user = new User(id: 1, username: "akira@test.com", password: "secret", name: "akira")
        micropostService.findByUser(1, new PageParams()) >> (0..1).collect {
            Micropost post = new Micropost(id: it, content: "content${it}", user: user, createdAt: new Date())
            return PostDTO.newInstance(post, null)
        }


        when:
        def response = perform(get("/api/users/${user.id}/microposts/"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$', hasSize(2)))
            andExpect(jsonPath('$[0].content', is("content0")))
            andExpect(jsonPath('$[0].isMyPost', nullValue()))
            andExpect(jsonPath('$[0].user.name', is("akira")))
            andExpect(jsonPath('$[1].content', is("content1")))
        }
    }

    def "can list my microposts when signed in"() {
        given:
        User user = signIn()
        micropostService.findMyPosts(new PageParams()) >> (0..1).collect {
            Micropost post = new Micropost(id: it, content: "content${it}", user: user, createdAt: new Date())
            return PostDTO.newInstance(post, true)
        }


        when:
        def response = perform(get("/api/users/me/microposts"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$', hasSize(2)))
            andExpect(jsonPath('$[0].content', is("content0")))
            andExpect(jsonPath('$[0].isMyPost', is(true)))
            andExpect(jsonPath('$[0].user.name', is(user.name)))
            andExpect(jsonPath('$[1].content', is("content1")))
        }
    }

    def "can not list my microposts when signed in"() {
        given:
        micropostService.findMyPosts(new PageParams()) >> []

        when:
        def response = perform(get("/api/users/me/microposts"))

        then:
        with(response) {
            andExpect(status().isUnauthorized())
        }
    }

}
