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

import javax.annotation.Nullable;
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
                                @RequestParam(value = "sinceId", required = false) @Nullable Long sinceId,
                                @RequestParam(value = "maxId", required = false) @Nullable Long maxId,
                                @RequestParam(value = "count", required = false) @Nullable Integer count) {
        final User user = userRepository.findOne(userId);
        final Integer maxSize = Optional.ofNullable(count).orElse(DEFAULT_PAGE_SIZE);
        return micropostRepository.findByUser(user, sinceId, maxId, maxSize);
    }
}
