package com.myapp.controller

import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.repository.MicropostCustomRepository
import com.myapp.repository.MicropostRepository
import com.myapp.repository.UserRepository
import com.myapp.service.MicropostService
import com.myapp.service.MicropostServiceImpl
import com.myapp.service.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FeedControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    MicropostCustomRepository micropostCustomRepository

    @Autowired
    UserRepository userRepository;

    SecurityContextService securityContextService = Mock(SecurityContextService);

    def "can show feed"() {
        given:
        User user = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test"))
        micropostRepository.save(new Micropost(user: user, content: "content1"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(get("/api/feed"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("content1")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
                .andExpect(jsonPath('$[0].createdAt').exists())
                .andExpect(jsonPath('$[0].user.email', is("test1@test.com")))
                .andExpect(jsonPath('$[0].user.avatarHash', is("94fba03762323f286d7c3ca9e001c541")))
    }

    @Override
    def controllers() {
        MicropostService micropostService = new MicropostServiceImpl(micropostRepository, micropostCustomRepository, securityContextService)
        return new FeedController(micropostService)
    }

}
