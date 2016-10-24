package com.myapp.service

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import com.myapp.dto.UserDTO
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

@SuppressWarnings("GroovyPointlessBoolean")
class UserServiceTest extends BaseServiceTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    UserService userService

    def setup() {
        userService = new UserServiceImpl(userRepository, userDTORepository, securityContextService)
    }

    def "can find a user when not signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
        userDTO.isMyself == null
    }

    def "can find a user when signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        securityContextService.currentUser() >> user

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
        userDTO.isMyself == true

        when:
        User anotherUser = userRepository.save(new User(username: "another@test.com", password: "secret", name: "another"))
        userDTO = userService.findOne(anotherUser.id).get()

        then:
        userDTO.isMyself == false
    }

}
