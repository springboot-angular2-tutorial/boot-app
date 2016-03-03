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
        userService = new UserServiceImpl(userRepository, securityContextService)
    }

    def "can find followings when not signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followings = userService.findFollowings(follower, new PageParams())

        then:
        followings.first().isMyself == null
    }

    def "can find followings when signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed1 = userRepository.save(new User(username: "followed1@test.com", password: "secret", name: "akira"))
        User followed2 = userRepository.save(new User(username: "followed2@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed2))
        securityContextService.currentUser() >> followed1

        when:
        def followings = userService.findFollowings(follower, new PageParams())

        then:
        followings.first().email == "followed2@test.com"
        followings.first().isMyself == false
        followings.last().isMyself == true
    }

    def "can find followers when not signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followers = userService.findFollowers(followed, new PageParams())

        then:
        followers.first().isMyself == null
    }

    def "can find followers when signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower1 = userRepository.save(new User(username: "follower1@test.com", password: "secret", name: "akira"))
        User follower2 = userRepository.save(new User(username: "follower2@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower1, followed: followed))
        relationshipRepository.save(new Relationship(follower: follower2, followed: followed))
        securityContextService.currentUser() >> follower1

        when:
        def followers = userService.findFollowers(followed, new PageParams())

        then:
        followers.first().email == "follower2@test.com"
        followers.first().isMyself == false
        followers.last().isMyself == true
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
