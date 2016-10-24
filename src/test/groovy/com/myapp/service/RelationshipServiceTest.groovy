package com.myapp.service

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import com.myapp.repository.RelatedUserCustomRepository
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

@SuppressWarnings("GroovyPointlessBoolean")
class RelationshipServiceTest extends BaseServiceTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelatedUserCustomRepository relatedUserCustomRepository

    @Autowired
    RelationshipRepository relationshipRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    RelationshipService relationshipService

    def setup() {
        relationshipService = new RelationshipServiceImpl(relatedUserCustomRepository, securityContextService)
    }

    def "can find followings when not signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followings = relationshipService.findFollowings(follower, new PageParams())

        then:
        followings.first().isMyself == null
    }

    def "can find followings when signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed1 = userRepository.save(new User(username: "followed1@test.com", password: "secret", name: "followed1"))
        User followed2 = userRepository.save(new User(username: "followed2@test.com", password: "secret", name: "followed2"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed2))
        securityContextService.currentUser() >> followed1

        when:
        def followings = relationshipService.findFollowings(follower, new PageParams())

        then:
        followings.first().name == "followed2"
        followings.first().email == null
        followings.first().isMyself == false
        followings.last().name == "followed1"
        followings.last().isMyself == true
    }

    def "can find followers when not signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followers = relationshipService.findFollowers(followed, new PageParams())

        then:
        followers.first().isMyself == null
    }

    def "can find followers when signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower1 = userRepository.save(new User(username: "follower1@test.com", password: "secret", name: "follower1"))
        User follower2 = userRepository.save(new User(username: "follower2@test.com", password: "secret", name: "follower2"))
        relationshipRepository.save(new Relationship(follower: follower1, followed: followed))
        relationshipRepository.save(new Relationship(follower: follower2, followed: followed))
        securityContextService.currentUser() >> follower1

        when:
        def followers = relationshipService.findFollowers(followed, new PageParams())

        then:
        followers.first().name == "follower2"
        followers.first().email == null
        followers.first().isMyself == false
        followers.last().name == "follower1"
        followers.last().isMyself == true
    }

}
