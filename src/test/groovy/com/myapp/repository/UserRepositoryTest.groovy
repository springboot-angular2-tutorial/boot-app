package com.myapp.repository

import com.myapp.config.DatasourceConfig
import com.myapp.config.QueryDSLConfig
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Transactional
@ActiveProfiles("test")
@ContextConfiguration(classes = [RepositoryTestConfig, DatasourceConfig, QueryDSLConfig])
class UserRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "findFollowings"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        User u1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        relationshipRepository.save(new Relationship(follower: user, followed: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))

        User u2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        relationshipRepository.save(new Relationship(follower: user, followed: u2))

        when:
        List<UserDTO> result = userRepository.findFollowings(user, currentUser, Optional.empty(), Optional.empty(), null)

        then:
        result[0].user.username == "test2@test.com"
        !result[0].userStats.isFollowedByMe()
        result[1].user.username == "test1@test.com"
        result[1].userStats.isFollowedByMe()
    }

    def "findFollowers"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        User u1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        relationshipRepository.save(new Relationship(followed: user, follower: u1))
        relationshipRepository.save(new Relationship(follower: currentUser, followed: u1))

        User u2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        relationshipRepository.save(new Relationship(followed: user, follower: u2))

        when:
        List<UserDTO> result = userRepository.findFollowers(user, currentUser, Optional.empty(), Optional.empty(), null)

        then:
        result[0].user.username == "test2@test.com"
        !result[0].userStats.isFollowedByMe()
        result[1].user.username == "test1@test.com"
        result[1].userStats.isFollowedByMe()
    }

}
