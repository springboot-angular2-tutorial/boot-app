package com.myapp.repository

import com.myapp.domain.Micropost
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import org.springframework.beans.factory.annotation.Autowired

class MicropostCustomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    MicropostCustomRepository micropostCustomRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "can find feed"() {
        given:
        User follower = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        User another = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "test2"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))
        [follower, followed, another].each { u ->
            micropostRepository.save(new Micropost(content: "test1", user: u))
            micropostRepository.save(new Micropost(content: "test2", user: u))
        }

        when:
        List<MicropostCustomRepository.Row> result = micropostCustomRepository.findAsFeed(follower, new PageParams()).collect()

        then:
        result.size() == 4
        result.first().micropost.user.id == followed.id
        result.last().micropost.user.id == follower.id
    }

    def "can find feed by since_id or max_id"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", user: user))
        Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user))
        Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user))

        when:
        List<MicropostCustomRepository.Row> result = micropostCustomRepository.findAsFeed(user, new PageParams(sinceId: post2.id)).collect()

        then:
        result.size() == 1
        result.first().micropost == post3

        when:
        result = micropostCustomRepository.findAsFeed(user, new PageParams(maxId: post2.id)).collect()

        then:
        result.size() == 1
        result.first().micropost == post1
    }

    def "can find posts by user"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post1 = micropostRepository.save(new Micropost(content: "test1", user: user))
        Micropost post2 = micropostRepository.save(new Micropost(content: "test2", user: user))
        Micropost post3 = micropostRepository.save(new Micropost(content: "test3", user: user))

        when:
        List<MicropostCustomRepository.Row> result = micropostCustomRepository.findByUser(user, new PageParams(sinceId: post2.id)).collect()

        then:
        result.size() == 1
        result.first().micropost == post3

        when:
        result = micropostCustomRepository.findByUser(user, new PageParams(maxId: post2.id)).collect()

        then:
        result.size() == 1
        result.first().micropost == post1
    }

}
