package com.myapp.controller

import com.myapp.Utils
import com.myapp.domain.User
import com.myapp.domain.UserStats
import com.myapp.dto.UserDTO
import com.myapp.dto.UserParams
import com.myapp.service.UserService
import com.myapp.service.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import spock.mock.DetachedMockFactory

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController)
class UserControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        UserService userService(DetachedMockFactory f) {
            return f.Mock(UserService)
        }
    }

    @Autowired
    UserService userService

    def "can signup"() {
        given:
        def email = "akirasosa@test.com"
        def password = "secret123"
        def name = "akira"

        when:
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        1 * userService.create(new UserParams(email, password, name))
        response.andExpect(status().isOk())
    }

    def "can not signup when email is duplicated"() {
        given:
        def email = "akirasosa@test.com"
        def password = "secret123"
        def name = "akira1"
        userService.create(_ as UserParams) >> {
            throw new DataIntegrityViolationException("")
        }

        when:
        def response = perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        with(response) {
            andExpect(status().isBadRequest())
            andExpect(jsonPath('$.code', is("email_already_taken")))
        }
    }

    def "can list users when signed in"() {
        given:
        signIn()
        userService.findAll(_ as PageRequest) >> {
            List<UserDTO> content = (0..1).collect {
                User u = new User(id: it, username: "test${it}@test.com", password: "secret", name: "test${it}")
                UserDTO.builder()
                        .id(u.id)
                        .name(u.name)
                        .avatarHash(Utils.md5(u.username))
                        .build()
            }
            return new PageImpl<>(content)
        }

        when:
        def response = perform(get("/api/users"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$.content').exists())
            andExpect(jsonPath('$.content', hasSize(2)))
            andExpect(jsonPath('$.content[0].name', is("test0")))
            andExpect(jsonPath('$.content[0].email', nullValue()))
            andExpect(jsonPath('$.content[0].avatarHash', is("17c9ea0d5cb514cd00d3a71eb312b9dc")))
            andExpect(jsonPath('$.content[1].name', is("test1")))
        }
    }

    def "can not list users when not signed in"() {
        given:
        userService.findAll(_ as PageRequest) >> []

        when:
        def response = perform(get("/api/users"))

        then:
        response.andExpect(status().isUnauthorized())
    }

    def "can show user"() {
        given:
        userService.findOne(1) >> {
            User user = new User(id: 1, username: "test1@test.com", password: "secret", name: "test")
            UserStats userStats = new UserStats(3, 2, 1)
            UserDTO userDTO = UserDTO.builder()
                    .id(user.id)
                    .name(user.name)
                    .avatarHash(Utils.md5(user.username))
                    .userStats(userStats)
                    .build()
            return Optional.of(userDTO)
        }

        when:
        def response = perform(get("/api/users/1"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$.name', is("test")))
            andExpect(jsonPath('$.email', nullValue()))
            andExpect(jsonPath('$.avatarHash', is("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath('$.isFollowedByMe', nullValue()))
            andExpect(jsonPath('$.userStats').exists())
            andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
            andExpect(jsonPath('$.userStats.followingCnt', is(2)))
            andExpect(jsonPath('$.userStats.followerCnt', is(1)))
        }
    }

    def "can not show user when user was not found"() {
        given:
        userService.findOne(1) >> { throw new UserNotFoundException() }

        when:
        def response = perform(get("/api/users/1"))

        then:
        with(response) {
            andExpect(status().isNotFound())
        }
    }

    def "can show logged in user when signed in"() {
        given:
        User user = signIn()
        userService.findMe() >> {
            UserStats userStats = new UserStats(3, 2, 1)
            UserDTO userDTO = UserDTO.builder()
                    .id(user.id)
                    .name(user.name)
                    .email(user.username)
                    .avatarHash(Utils.md5(user.username))
                    .userStats(userStats)
                    .isFollowedByMe(false)
                    .build()
            Optional.of(userDTO)
        }

        when:
        def response = perform(get("/api/users/me"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$.name', is("test")))
            andExpect(jsonPath('$.email', is("test@test.com")))
            andExpect(jsonPath('$.avatarHash', is("b642b4217b34b1e8d3bd915fc65c4452")))
            andExpect(jsonPath('$.isFollowedByMe', is(false)))
            andExpect(jsonPath('$.userStats').exists())
            andExpect(jsonPath('$.userStats.micropostCnt', is(3)))
            andExpect(jsonPath('$.userStats.followingCnt', is(2)))
            andExpect(jsonPath('$.userStats.followerCnt', is(1)))
        }
    }

    def "can not show logged in user when not signed in"() {
        when:
        def response = perform(get("/api/users/me"))

        then:
        with(response) {
            andExpect(status().isUnauthorized())
        }
    }

    def "can update me"() {
        given:
        signIn()
        String email = "test2@test.com"
        String password = "very secret"
        String name = "new name"
        userService.updateMe(new UserParams(email, password, name)) >> {
            return new User(id: 1, username: email, password: password, name: name)
        }

        when:
        def response = perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: email, password: password, name: name))
        )

        then:
        with(response) {
            andExpect(status().isOk())
        }
    }

    def "can not update me when not signed in"() {
        when:
        def response = perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test", password: "test", name: "test"))
        )

        then:
        with(response) {
            andExpect(status().isUnauthorized())
        }
    }

    def "can not update me when parameter is invalid"() {
        given:
        signIn()

        when:
        // password is too short
        def response = perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test@test.com", password: "a", name: "test"))
        )

        then:
        with(response) {
            andExpect(status().isBadRequest())
        }
    }

}
