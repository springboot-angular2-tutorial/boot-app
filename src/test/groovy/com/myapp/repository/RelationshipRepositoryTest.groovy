package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired

class RelationshipRepositoryTest extends BaseRepositoryTest {

    @Autowired
    RelationshipRepository relationshipRepository

    @Autowired
    UserRepository userRepository

    def "can find by follower and followed"() {
        given:
        User follower = new User(username: "akira@test.com", password: "secret", name: "akira")
        User followed = new User(username: "satoru@test.com", password: "secret", name: "satoru")
        userRepository.save([follower, followed])
        Relationship relationship = new Relationship(follower: follower, followed: followed)

        when:
        def result = relationshipRepository.findOneByFollowerAndFollowed(follower, followed)

        then:
        !result.isPresent()

        when:
        relationshipRepository.save(relationship)
        result = relationshipRepository.findOneByFollowerAndFollowed(follower, followed)

        then:
        result.get() == relationship
    }
}
