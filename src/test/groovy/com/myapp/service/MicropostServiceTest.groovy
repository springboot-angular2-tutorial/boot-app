package com.myapp.service

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Micropost
import com.myapp.domain.User
import com.myapp.repository.MicropostRepository
import com.myapp.repository.RepositoryTestConfig
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification

@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig])
class MicropostServiceTest extends Specification {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Shared
    MicropostService micropostService

    def setup() {
        micropostService = new MicropostServiceImpl(micropostRepository, securityContextService)
    }

    def "can delete micropost when have a permission"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test"))
        securityContextService.currentUser() >> user

        when:
        micropostService.delete(post.id)

        then:
        micropostRepository.count() == 0
    }

    def "can not delete micropost when have no permission"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test"))

        when:
        micropostService.delete(post.id)

        then:
        thrown(NotPermittedException)
        micropostRepository.count() == 1
    }

}
