package com.myapp.controller;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}/microposts")
public class UserMicropostController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final UserRepository userRepository;
    private final MicropostRepository micropostRepository;

    @Autowired
    public UserMicropostController(UserRepository userRepository, MicropostRepository micropostRepository) {
        this.userRepository = userRepository;
        this.micropostRepository = micropostRepository;
    }

    @RequestMapping
    public List<Micropost> list(@PathVariable("userId") Long userId,
                                @RequestParam("sinceId") Optional<Long> sinceId,
                                @RequestParam("maxId") Optional<Long> maxId,
                                @RequestParam("count") Optional<Integer> count) {
        User user = userRepository.findOne(userId);
        return micropostRepository
                .findByUser(user, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }
}
