package com.myapp.repository

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import org.springframework.beans.factory.annotation.Autowired

class RelatedUserCustomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelatedUserCustomRepository relatedUserCustomRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "findFollowings"() {
        given:
        User follower = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User followed1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        User followed2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        Relationship r2 = relationshipRepository.save(new Relationship(follower: follower, followed: followed2))

        when:
        List<RelatedUserCustomRepository.Row> result = relatedUserCustomRepository.findFollowings(follower, new PageParams())

        then:
        result[0].user.username == "test2@test.com"
        result[0].relationship.id == r2.id
        result[1].user.username == "test1@test.com"
        result[1].relationship.id == r1.id
    }

    def "findFollowers"() {
        given:
        User followed = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User follower1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        User follower2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        Relationship r1 = relationshipRepository.save(new Relationship(followed: followed, follower: follower1))
        Relationship r2 = relationshipRepository.save(new Relationship(followed: followed, follower: follower2))

        when:
        List<RelatedUserCustomRepository.Row> result = relatedUserCustomRepository.findFollowers(followed, new PageParams())

        then:
        result[0].user.username == "test2@test.com"
        result[0].relationship.id == r2.id
        result[1].user.username == "test1@test.com"
        result[1].relationship.id == r1.id
    }

}
