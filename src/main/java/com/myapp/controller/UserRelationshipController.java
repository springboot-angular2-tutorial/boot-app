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

import java.util.List;
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
    public List<User> followings(@PathVariable("userId") Long userId,
                                 @RequestParam("sinceId") Optional<Long> sinceId,
                                 @RequestParam("maxId") Optional<Long> maxId,
                                 @RequestParam("count") Optional<Integer> count) {
        User user = userRepository.findOne(userId);
        return userRepository
                .findFollowings(user, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }

    @RequestMapping("/followers")
    public List<User> followers(@PathVariable("userId") Long userId,
                                @RequestParam("sinceId") Optional<Long> sinceId,
                                @RequestParam("maxId") Optional<Long> maxId,
                                @RequestParam("count") Optional<Integer> count) {
        User user = userRepository.findOne(userId);
        return userRepository
                .findFollowers(user, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }
}
