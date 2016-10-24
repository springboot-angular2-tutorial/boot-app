package com.myapp.controller

import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.repository.RelatedUserCustomRepository
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import com.myapp.service.RelationshipService
import com.myapp.service.RelationshipServiceImpl
import com.myapp.service.SecurityContextService
import com.myapp.service.UserService
import com.myapp.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.isEmptyOrNullString
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserRelationshipControllerTest extends BaseControllerTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    @Autowired
    RelatedUserCustomRepository relatedUserCustomRepository

    SecurityContextService securityContextService = Mock(SecurityContextService);

    @Override
    def controllers() {
        final RelationshipService relationshipService = new RelationshipServiceImpl(relatedUserCustomRepository, securityContextService)
        return new UserRelationshipController(userRepository, relationshipService)
    }

    def "can list followings"() {
        given:
        User user1 = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "satoru@test.com", password: "secret", name: "satoru"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: user1, followed: user2))
        securityContextService.currentUser() >> userRepository.save(new User(username: "current@test.com", password: "secret", name: "akira"))

        when:
        def response = perform(get("/api/users/${user1.id}/followings"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].name', is("satoru")))
                .andExpect(jsonPath('$[0].email', isEmptyOrNullString()))
                .andExpect(jsonPath('$[0].avatarHash', is("f296925ce2e05bd387bf65d6ac27d2fe")))
                .andExpect(jsonPath('$[0].isMyself', is(false)))
                .andExpect(jsonPath('$[0].userStats').exists())
                .andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
    }

    def "can list followers"() {
        given:
        User user1 = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "satoru@test.com", password: "secret", name: "satoru"))
        Relationship r1 = relationshipRepository.save(new Relationship(follower: user2, followed: user1))
        securityContextService.currentUser() >> userRepository.save(new User(username: "current@test.com", password: "secret", name: "akira"))

        when:
        def response = perform(get("/api/users/${user1.id}/followers"))

        then:
        response
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].name', is("satoru")))
                .andExpect(jsonPath('$[0].email', isEmptyOrNullString()))
                .andExpect(jsonPath('$[0].avatarHash', is("f296925ce2e05bd387bf65d6ac27d2fe")))
                .andExpect(jsonPath('$[0].isMyself', is(false)))
                .andExpect(jsonPath('$[0].userStats').exists())
                .andExpect(jsonPath('$[0].relationshipId', is(r1.id.intValue())))
    }
}
