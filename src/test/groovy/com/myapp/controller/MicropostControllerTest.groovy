package com.myapp.controller

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.repository.MicropostRepository
import com.myapp.repository.RepositoryTestConfig
import com.myapp.repository.UserRepository
import com.myapp.service.MicropostService
import com.myapp.service.MicropostServiceImpl
import com.myapp.service.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig])
class MicropostControllerTest extends BaseControllerTest {

    @Autowired
    MicropostRepository micropostRepository;

    @Autowired
    UserRepository userRepository;

    MicropostService micropostService;

    SecurityContextService securityContextService = Mock(SecurityContextService);

    @Override
    def controllers() {
        micropostService = new MicropostServiceImpl(micropostRepository, securityContextService)
        return new MicropostController(micropostRepository, micropostService, securityContextService)
    }

    def "can create a micropost"() {
        given:
        String content = "my content"
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(post("/api/microposts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(content: content))
        )

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', is(content)))
        micropostRepository.count() == 1
    }

    def "can delete a micropost"() {
        given:
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        Micropost micropost = micropostRepository.save(new Micropost(user, "content"))
        securityContextService.currentUser() >> user

        when:
        def response = perform(delete("/api/microposts/${micropost.id}"))

        then:
        response.andExpect(status().isOk())
        micropostRepository.count() == 0
    }

}
