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
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        User u1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: user, followed: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))

        User u2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        Relationship r2 = relationshipRepository.save(new Relationship(follower: user, followed: u2))

        when:
        List<RelatedUserCustomRepository.Row> result = relatedUserCustomRepository.findFollowings(user, currentUser, new PageParams())

        then:
        result[0].user.username == "test2@test.com"
        !result[0].userStats.isFollowedByMe()
        result[0].relationship.id == r2.id
        result[1].user.username == "test1@test.com"
        result[1].userStats.isFollowedByMe()
        result[1].relationship.id == r1.id
    }

    def "findFollowers"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        User u1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        Relationship r1 = relationshipRepository.save(new Relationship(followed: user, follower: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))

        User u2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        Relationship r2 = relationshipRepository.save(new Relationship(followed: user, follower: u2))

        when:
        List<RelatedUserCustomRepository.Row> result = relatedUserCustomRepository.findFollowers(user, currentUser, new PageParams())

        then:
        result[0].user.username == "test2@test.com"
        !result[0].userStats.isFollowedByMe()
        result[0].relationship.id == r2.id
        result[1].user.username == "test1@test.com"
        result[1].userStats.isFollowedByMe()
        result[1].relationship.id == r1.id
    }

}
