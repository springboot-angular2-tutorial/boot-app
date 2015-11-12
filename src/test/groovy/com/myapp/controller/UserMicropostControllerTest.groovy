package com.myapp.controller

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.repository.MicropostRepository
import com.myapp.repository.RepositoryTestConfig
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig])
class UserMicropostControllerTest extends BaseControllerTest {

    @Autowired
    private final UserRepository userRepository

    @Autowired
    private final MicropostRepository micropostRepository

    @Override
    def controllers() {
        return new UserMicropostController(userRepository, micropostRepository)
    }

    def "can list microposts"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        micropostRepository.save(new Micropost(user: user, content: "my content"))

        when:
        def response = perform(get("/api/users/${user.id}/microposts/"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', hasSize(1)))
                .andExpect(jsonPath('$.content[0].content', is("my content")))
                .andDo(MockMvcResultHandlers.print())
    }

}
