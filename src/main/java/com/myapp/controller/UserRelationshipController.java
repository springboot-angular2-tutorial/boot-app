package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}")
public class UserRelationshipController {

    private static final Integer DEFAULT_PAGE_SIZE = 5;
    private final UserRepository userRepository;

    @Autowired
    public UserRelationshipController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/followings")
    public Page<User> followings(@PathVariable("userId") Long userId,
                                 @RequestParam(value = "page") Optional<Integer> page,
                                 @RequestParam(value = "size") Optional<Integer> size) {
        User user = userRepository.findOne(userId);
        Pageable pageRequest = new PageRequest(page.orElse(1) - 1,
                size.orElse(DEFAULT_PAGE_SIZE));

        return userRepository.findFollowings(user, pageRequest);
    }

    @RequestMapping("/followers")
    public Page<User> followers(@PathVariable("userId") Long userId,
                                @RequestParam(value = "page") Optional<Integer> page,
                                @RequestParam(value = "size") Optional<Integer> size) {
        User user = userRepository.findOne(userId);
        Pageable pageRequest = new PageRequest(page.orElse(1) - 1,
                size.orElse(DEFAULT_PAGE_SIZE));

        return userRepository.findFollowers(user, pageRequest);
    }
}
