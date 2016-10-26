package com.myapp.repository

import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired

class UserCustomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UserRepository userRepository

    @Autowired
    UserCustomRepository userCustomRepository

    def "findOne"() {
        given:
        User user = userRepository.save(new User(username: "akira@test.com", password: "secret", name: "akira"))

        when:
        UserCustomRepository.Row result = userCustomRepository.findOne(user.id).get()

        then:
        result.user == user
        result.userStats.followerCnt == 0
        result.userStats.followingCnt == 0
        result.userStats.micropostCnt == 0
    }

}
