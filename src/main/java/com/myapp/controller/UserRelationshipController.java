package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.dto.UserDTO;
import com.myapp.repository.UserRepository;
import com.myapp.service.SecurityContextService;
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
    private final SecurityContextService securityContextService;

    @Autowired
    public UserRelationshipController(UserRepository userRepository, SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.securityContextService = securityContextService;
    }

    @RequestMapping("/followings")
    public List<UserDTO> followings(@PathVariable("userId") Long userId,
                                    @RequestParam("sinceId") Optional<Long> sinceId,
                                    @RequestParam("maxId") Optional<Long> maxId,
                                    @RequestParam("count") Optional<Integer> count) {
        final User user = userRepository.findOne(userId);
        final User currentUser = securityContextService.currentUser();
        return userRepository
                .findFollowings(user, currentUser, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }

    @RequestMapping("/followers")
    public List<UserDTO> followers(@PathVariable("userId") Long userId,
                                   @RequestParam("sinceId") Optional<Long> sinceId,
                                   @RequestParam("maxId") Optional<Long> maxId,
                                   @RequestParam("count") Optional<Integer> count) {
        final User user = userRepository.findOne(userId);
        final User currentUser = securityContextService.currentUser();
        return userRepository
                .findFollowers(user, currentUser, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }
}
