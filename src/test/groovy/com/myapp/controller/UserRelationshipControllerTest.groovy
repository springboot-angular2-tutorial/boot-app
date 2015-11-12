package com.myapp.controller

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.RepositoryTestConfig
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig])
class UserRelationshipControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    @Override
    def controllers() {
        return new UserRelationshipController(userRepository)
    }

    def "can list followings"() {
        given:
        User user1 = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "satoru@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: user1, followed: user2))

        when:
        def response = perform(get("/api/users/${user1.id}/followings"))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', hasSize(1)))
                .andExpect(jsonPath('$.content[0].email', is("satoru@test.com")))
    }

    def "can list followers"() {
        given:
        User user1 = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "satoru@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: user2, followed: user1))

        when:
        def response = perform(get("/api/users/${user1.id}/followers"))

        then:
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.content').exists())
                .andExpect(jsonPath('$.content', hasSize(1)))
                .andExpect(jsonPath('$.content[0].email', is("satoru@test.com")))
    }
}
