package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.UserRepository;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;

    @Autowired
    public UserRelationshipController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping("/followings")
    public List<RelatedUserDTO> followings(@PathVariable("userId") Long userId,
                                           @RequestParam("sinceId") Optional<Long> sinceId,
                                           @RequestParam("maxId") Optional<Long> maxId,
                                           @RequestParam("count") Optional<Integer> count) {
        final User user = userRepository.findOne(userId);
        return userService.findFollowings(user, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }

    @RequestMapping("/followers")
    public List<RelatedUserDTO> followers(@PathVariable("userId") Long userId,
                                          @RequestParam("sinceId") Optional<Long> sinceId,
                                          @RequestParam("maxId") Optional<Long> maxId,
                                          @RequestParam("count") Optional<Integer> count) {
        final User user = userRepository.findOne(userId);
        return userService.findFollowers(user, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }
}
