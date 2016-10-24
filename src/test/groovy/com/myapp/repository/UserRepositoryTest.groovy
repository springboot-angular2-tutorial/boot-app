package com.myapp.repository

import com.myapp.domain.User
import com.myapp.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    RelationshipRepository relationshipRepository

    def "findOne"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        when:
        UserDTO result = userRepository.findOne(user.id, currentUser).get();

        then:
        result.id == user.id
        !result.userStats.followedByMe
    }

}
