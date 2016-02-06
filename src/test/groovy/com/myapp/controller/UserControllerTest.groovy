package com.myapp.controller

import com.myapp.auth.TokenHandler
import com.myapp.domain.Micropost
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.repository.MicropostRepository
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import com.myapp.service.SecurityContextService
import com.myapp.service.UserService
import com.myapp.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    RelationshipRepository relationshipRepository

    TokenHandler tokenHandler

    UserService userService

    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Override
    def controllers() {
        userService = new UserServiceImpl(userRepository)
        tokenHandler = new TokenHandler("secret", userService)
        return new UserController(userRepository, userService, securityContextService, tokenHandler)
    }

    def "can signup"() {
        def email = "akirasosa@test.com"
        def password = "secret123"
        def name = "akira"

        when:
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        userRepository.count() == 1
        userRepository.findAll().get(0).username == email
    }

    def "can not signup when email is duplicated"() {
        def email = "akirasosa@test.com"
        def password = "secret123"
        def name = "akira1"

        when:
        userRepository.save(new User(username: email, password: password, name: "akira0"))
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.code', is("email_already_taken")))
    }

    def "can list users"() {
        given:
        2.times {
            userRepository.save(new User(username: "test${it}@test.com", password: "secret", name: "test${it}"))
        }

        when:
        def response = perform(get("/api/users"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', hasSize(2)))
                .andExpect(jsonPath('$.content[0].email', is("test0@test.com")))
                .andExpect(jsonPath('$.content[1].email', is("test1@test.com")))
    }

    def "can show user"() {
        given:
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        prepareMicroposts(user)
        prepareRelationships(user)

        when:
        def response = perform(get("/api/users/${user.id}"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(user.name)))
                .andExpect(jsonPath('$.userStats').exists())
                .andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
                .andExpect(jsonPath('$.userStats.followingCnt', is(2)))
                .andExpect(jsonPath('$.userStats.followerCnt', is(1)))
    }

    def "can show logged in user"() {
        given:
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        securityContextService.currentUser() >> user
        prepareMicroposts(user)
        prepareRelationships(user)

        when:
        def response = perform(get("/api/users/me"))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(user.name)))
                .andExpect(jsonPath('$.userStats').exists())
                .andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
                .andExpect(jsonPath('$.userStats.followingCnt', is(2)))
                .andExpect(jsonPath('$.userStats.followerCnt', is(1)))
    }

    def "can update me"() {
        given:
        User user = userRepository.save(new User(username: "test@test.com", password: "secret", name: "test"))
        securityContextService.currentUser() >> user
        String email = "test2@test.com"
        String password = "very secret"
        String name = "new name"

        when:
        def response = perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        response
                .andExpect(status().isOk())
                .andExpect(header().string("X-AUTH-TOKEN", not(isEmptyOrNullString())))
        user.getUsername() == email
        user.getName() == name
    }

    private prepareMicroposts(User user) {
        3.times {
            micropostRepository.save(new Micropost(user, "content${it}"))
        }
    }

    private void prepareRelationships(User user) {
        List<User> otherUsers = (1..2).collect {
            User u = userRepository.save(new User(username: "test${it}@test.com", password: "secret", name: "test"))
            relationshipRepository.save(new Relationship(follower: user, followed: u))
            return u
        }
        relationshipRepository.save(new Relationship(follower: otherUsers.first(), followed: user))
    }

}
