package com.myapp.service

import com.myapp.domain.Micropost
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.PageParams
import com.myapp.dto.PostDTO
import com.myapp.repository.MicropostRepository
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

class MicropostServiceTest extends BaseServiceTest {

    @Autowired
    MicropostRepository micropostRepository

    @Autowired
    UserRepository userRepository

    SecurityContextService securityContextService = Mock(SecurityContextService)

    @Shared
    MicropostService micropostService

    @Autowired
    RelationshipRepository relationshipRepository

    def setup() {
        micropostService = new MicropostServiceImpl(micropostRepository, securityContextService)
    }

    def "can delete micropost when have a permission"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test"))
        securityContextService.currentUser() >> user

        when:
        micropostService.delete(post.id)

        then:
        micropostRepository.count() == 0
    }

    def "can not delete micropost when have no permission"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        Micropost post = micropostRepository.save(new Micropost(user: user, content: "test"))

        when:
        micropostService.delete(post.id)

        then:
        thrown(NotPermittedException)
        micropostRepository.count() == 1
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "can find posts as feed"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        micropostRepository.save(new Micropost(user: user, content: "my content"))
        securityContextService.currentUser() >> user

        User followed = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "test1"))
        relationshipRepository.save(new Relationship(follower: user, followed: followed))
        micropostRepository.save(new Micropost(user: followed, content: "follower content"))

        when:
        List<PostDTO> posts = micropostService.findAsFeed(new PageParams())

        then:
        posts.first().content == 'follower content'
        posts.first().isMyPost == false
        posts.last().isMyPost == true
    }

    def "can find posts by user when not signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        micropostRepository.save(new Micropost(user: user, content: "my content"))

        when:
        List<PostDTO> posts = micropostService.findByUser(user, new PageParams())

        then:
        posts.first().isMyPost == null;
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "can find posts by user when signed in"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        securityContextService.currentUser() >> user

        when:
        micropostRepository.save(new Micropost(user: user, content: "my content"))
        List<PostDTO> posts = micropostService.findByUser(user, new PageParams())

        then:
        posts.first().isMyPost == true;

        when:
        User anotherUser = userRepository.save(new User(username: "satoru@test.com", password: "secret", name: "satoru"))
        micropostRepository.save(new Micropost(user: anotherUser, content: "my content"))
        List<PostDTO> anotherPosts = micropostService.findByUser(anotherUser, new PageParams())

        then:
        anotherPosts.first().isMyPost == false;
    }
}
