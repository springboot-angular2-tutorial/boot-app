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

    RelationshipService relationshipService

    def setup() {
        relationshipService = new RelationshipServiceImpl(relationshipRepository, relatedUserCustomRepository, userRepository, securityContextService)
    }

    def "can find followings when not signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followings = relationshipService.findFollowings(follower.id, new PageParams())

        then:
        followings.first().isFollowedByMe == null
    }

    def "can find followings when signed in"() {
        given:
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        User followed1 = userRepository.save(new User(username: "followed1@test.com", password: "secret", name: "followed1"))
        User followed2 = userRepository.save(new User(username: "followed2@test.com", password: "secret", name: "followed2"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed1))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed2))
        signIn(followed1)

        when:
        def followings = relationshipService.findFollowings(follower.id, new PageParams())

        then:
        followings.first().name == "followed2"
        followings.first().isFollowedByMe == false
        followings.last().name == "followed1"
        followings.last().isFollowedByMe == false
    }

    def "can find followers when not signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower = userRepository.save(new User(username: "follower@test.com", password: "secret", name: "akira"))
        relationshipRepository.save(new Relationship(follower: follower, followed: followed))

        when:
        def followers = relationshipService.findFollowers(followed.id, new PageParams())

        then:
        followers.first().isFollowedByMe == null
    }

    def "can find followers when signed in"() {
        given:
        User followed = userRepository.save(new User(username: "followed@test.com", password: "secret", name: "akira"))
        User follower1 = userRepository.save(new User(username: "follower1@test.com", password: "secret", name: "follower1"))
        User follower2 = userRepository.save(new User(username: "follower2@test.com", password: "secret", name: "follower2"))
        relationshipRepository.save(new Relationship(follower: follower1, followed: followed))
        relationshipRepository.save(new Relationship(follower: follower2, followed: followed))
        signIn(follower1)

        when:
        def followers = relationshipService.findFollowers(followed.id, new PageParams())

        then:
        followers.first().name == "follower2"
        followers.first().isFollowedByMe == false
        followers.last().name == "follower1"
        followers.last().isFollowedByMe == false
    }

    def "can follow and unfollow"() {
        given:
        User currentUser = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "akira"))
        User targetUser = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "akira"))
        signIn(currentUser)

        when:
        relationshipService.follow(targetUser.id)

        then:
        def relationShip = relationshipRepository.findAll().first()
        relationShip.follower == currentUser
        relationShip.followed == targetUser

        when:
        relationshipService.unfollow(targetUser.id)

        then:
        relationshipRepository.count() == 0
    }

}
