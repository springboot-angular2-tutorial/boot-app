package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired

class RelationshipRepositoryTest extends BaseRepositoryTest {

    @Autowired
    RelationshipRepository relationshipRepository

    @Autowired
    UserRepository userRepository

    def "can find one by follower and followed"() {
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

    def "can find all by follower and followed in"() {
        given:
        User follower = new User(username: "akira@test.com", password: "secret", name: "akira")
        User followed1 = new User(username: "followed1@test.com", password: "secret", name: "satoru")
        User followed2 = new User(username: "followed2@test.com", password: "secret", name: "satoru")
        userRepository.save([follower, followed1, followed2])
        Relationship relationship1 = new Relationship(follower: follower, followed: followed1)
        Relationship relationship2 = new Relationship(follower: follower, followed: followed2)
        relationshipRepository.save([relationship1, relationship2])

        when:
        List<Relationship> result = relationshipRepository.findAllByFollowerAndFollowedIn(follower, [followed1]).collect()

        then:
        result.size() == 1
        result.first().followed == followed1
    }
}
