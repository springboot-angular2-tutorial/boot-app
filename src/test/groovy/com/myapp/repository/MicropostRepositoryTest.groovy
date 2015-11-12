package com.myapp.repository

import com.myapp.config.DatasourceConfig
import com.myapp.domain.Micropost
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
class MicropostRepositoryTest extends Specification {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "can find feed"() {
        given:
        User follower = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "akira"))
        User another = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))
        [follower, followed, another].each { u ->
            micropostRepository.save(new Micropost(content: "test1", user: u))
            micropostRepository.save(new Micropost(content: "test2", user: u))
        }

        when:
        Page<Micropost> result = micropostRepository.findAsFeed(follower, new PageRequest(0, 3))

        then:
        result.totalElements == 4
        result.totalPages == 2
        result.size == 3
        result[0].user == followed
        result[0].content == "test2"
    }
}
