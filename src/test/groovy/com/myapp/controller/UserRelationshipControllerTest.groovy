package com.myapp.controller

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import com.myapp.dto.RelatedUserDTO
import com.myapp.service.RelationshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.nullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserRelationshipController)
class UserRelationshipControllerTest extends BaseControllerTest2 {

    @TestConfiguration
    static class Config {
        @Bean
        RelationshipService relationshipService(DetachedMockFactory f) {
            return f.Mock(RelationshipService)
        }
    }

    @Autowired
    RelationshipService relationshipService

    def "can list followings"() {
        given:
        User follower = new User(id: 1, username: "akira@test.com", password: "secret", name: "akira")
        User followed1 = new User(id: 2, username: "test1@test.com", password: "secret", name: "test1")
        User followed2 = new User(id: 3, username: "test2@test.com", password: "secret", name: "test2")
        Relationship r1 = new Relationship(id: 1, follower: follower, followed: followed1)
        Relationship r2 = new Relationship(id: 2, follower: follower, followed: followed2)
        relationshipService.findFollowings(follower.id, new PageParams()) >> [
                RelatedUserDTO.newInstance(followed1, r1, null, null),
                RelatedUserDTO.newInstance(followed2, r2, null, null),
        ]

        when:
        def response = perform(get("/api/users/${follower.id}/followings"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$[0].name', is("test1")))
            andExpect(jsonPath('$[0].email', nullValue()))
            andExpect(jsonPath('$[0].avatarHash', is("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath('$[0].isMyself', nullValue()))
            andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
            andExpect(jsonPath('$[1].name', is("test2")))
        }
    }

    def "can list followers"() {
        given:
        User followed = new User(id: 1, username: "akira@test.com", password: "secret", name: "akira")
        User follower1 = new User(id: 2, username: "test1@test.com", password: "secret", name: "test1")
        User follower2 = new User(id: 3, username: "test2@test.com", password: "secret", name: "test2")
        Relationship r1 = new Relationship(id: 1, follower: follower1, followed: followed)
        Relationship r2 = new Relationship(id: 2, follower: follower2, followed: followed)
        relationshipService.findFollowers(followed.id, new PageParams()) >> [
                RelatedUserDTO.newInstance(follower1, r1, null, null),
                RelatedUserDTO.newInstance(follower2, r2, null, null),
        ]

        when:
        def response = perform(get("/api/users/${followed.id}/followers"))

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$[0].name', is("test1")))
            andExpect(jsonPath('$[0].email', nullValue()))
            andExpect(jsonPath('$[0].avatarHash', is("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath('$[0].isMyself', nullValue()))
            andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
            andExpect(jsonPath('$[1].name', is("test2")))
        }
    }
}
