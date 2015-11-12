package com.myapp.repository

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Relationship
import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig])
class UserRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "findFollowings"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        3.times {
            User u = userRepository.save(new User(username: "test${it}@test.com", password: "secret", name: "akira"))
            relationshipRepository.save(new Relationship(follower: user, followed: u))
        }

        when:
        Page<User> result = userRepository.findFollowings(user, new PageRequest(0, 2))

        then:
        result.totalElements == 3
        result.totalPages == 2
        result.size == 2
        result[0].username == "test0@test.com"
        result[1].username == "test1@test.com"
    }

    def "findFollowers"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        3.times {
            User u = userRepository.save(new User(username: "test${it}@test.com", password: "secret", name: "akira"))
            relationshipRepository.save(new Relationship(follower: u, followed: user))
        }

        when:
        Page<User> result = userRepository.findFollowers(user, new PageRequest(0, 2))

        then:
        result.totalElements == 3
        result.totalPages == 2
        result.size == 2
        result[0].username == "test0@test.com"
        result[1].username == "test1@test.com"
    }

}
