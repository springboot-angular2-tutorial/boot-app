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

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserMicropostControllerTest extends BaseControllerTest {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private MicropostRepository micropostRepository

    @Autowired
    private MicropostCustomRepository micropostCustomRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Override
    def controllers() {
        final MicropostService micropostService = new MicropostServiceImpl(micropostRepository, micropostCustomRepository, securityContextService);
        return new UserMicropostController(userRepository, micropostService, securityContextService)
    }

    def "can list microposts"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        micropostRepository.save(new Micropost(user: user, content: "my content"))

        when:
        def response = perform(get("/api/users/${user.id}/microposts/"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("my content")))
                .andExpect(jsonPath('$[0].isMyPost', nullValue()))
    }

    def "can list my microposts"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        micropostRepository.save(new Micropost(user: user, content: "my content"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(get("/api/users/me/microposts/"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].content', is("my content")))
                .andExpect(jsonPath('$[0].isMyPost', is(true)))
    }

}
