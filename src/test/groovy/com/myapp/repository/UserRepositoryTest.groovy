package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    RelationshipRepository relationshipRepository

    @Autowired
    UserRepository userRepository

    def "findFollowingsIn"() {
        given:
        User follower = new User(username: "akira@test.com", password: "secret", name: "akira")
        User followed1 = new User(username: "test1@test.com", password: "secret", name: "test1")
        User followed2 = new User(username: "test2@test.com", password: "secret", name: "test2")
        userRepository.save([follower, followed1, followed2])
        relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed2))

        when:
        def result = userRepository.findFollowedBy(follower, [followed2])

        then:
        result.size() == 1
        result.first() == followed2
    }

}
