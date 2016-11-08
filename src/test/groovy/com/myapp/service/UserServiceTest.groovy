package com.myapp.service

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.UserDTO
import com.myapp.dto.UserParams
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserCustomRepository
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@SuppressWarnings("GroovyPointlessBoolean")
class UserServiceTest extends BaseServiceTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    UserCustomRepository userCustomRepository

    @Autowired
    RelationshipRepository relationshipRepository

    UserService userService

    def setup() {
        userService = new UserServiceImpl(userRepository, userCustomRepository, relationshipRepository, securityContextService)
    }

    def "can find a user when not signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
        userDTO.id == user.id
        userDTO.isFollowedByMe == null // not signed in
    }

    def "can find a user with isFollowedByMe true when signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "akira"))
        signIn(currentUser)
        relationshipRepository.save(new Relationship(follower: currentUser, followed: user))

        when:
        UserDTO userDTO = userService.findOne(user.id).get()

        then:
        userDTO.id == user.id
        userDTO.isFollowedByMe == true
    }

    def "can find me"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        signIn(user)

        when:
        UserDTO userDTO = userService.findMe().get()

        then:
        userDTO.id == user.id
    }

    def "can find paged user list"() {
        given:
        //noinspection GroovyUnusedAssignment
        User user1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "akira"))

        when:
        PageRequest pageRequest = new PageRequest(1, 1)
        Page<UserDTO> page = userService.findAll(pageRequest)

        then:
        page.content.first().id == user2.id
        page.totalElements == 2
    }

    def "can create a user"() {
        given:
        UserParams params = new UserParams("test1@test.com", "secret", "test1")

        when:
        User user = userService.create(params)

        then:
        userRepository.count() == 1
        user.username == "test1@test.com"
    }

    def "can update a user"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        UserParams params = new UserParams("test2@test.com", "secret2", "test2")

        when:
        userService.update(user, params)

        then:
        user.username == "test2@test.com"
        user.name == "test2"

        when:
        params = new UserParams("test3@test.com", null, null)
        userService.update(user, params)

        then:
        user.username == "test3@test.com"
        user.name == "test2"
    }

    def "can update me"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        UserParams params = new UserParams("test2@test.com", "secret2", "test2")
        signIn(user)

        when:
        userService.updateMe(params)

        then:
        user.username == "test2@test.com"
        user.name == "test2"
    }

    def "loadUserByUsername"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))

        when:
        UserDetails userDetails = userService.loadUserByUsername("akira@test.com")

        then:
        user.username == userDetails.username

        when:
        userService.loadUserByUsername("test1@test.com")

        then:
        thrown(UsernameNotFoundException)
    }

}
