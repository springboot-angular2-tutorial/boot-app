package com.myapp.repository

import com.myapp.domain.User
import com.myapp.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

class UserCustomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    UserCustomRepository userCustomRepository

    def "findOne"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))
        User currentUser = userRepository.save(new User(username: "current@test.com", password: "secret", name: "current"))

        when:
        UserDTO result = userCustomRepository.findOne(user.id, currentUser).get();

        then:
        result.id == user.id
        !result.userStats.followedByMe
    }

    def "findAll"() {
        given:
        //noinspection GroovyUnusedAssignment
        User user1 = userRepository.save(new User(username: "test1@test.com", password: "secret", name: "akira"))
        User user2 = userRepository.save(new User(username: "test2@test.com", password: "secret", name: "akira"))

        when:
        PageRequest pageRequest = new PageRequest(1, 1)
        Page<UserDTO> page = userCustomRepository.findAll(pageRequest)

        then:
        page.content.first().id == user2.id
        page.totalElements == 2
    }

}
