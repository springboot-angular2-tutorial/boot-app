package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.UserRepository;
import com.myapp.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
public class UserRelationshipController {

    private final UserRepository userRepository;
    private final RelationshipService relationshipService;

    @Autowired
    public UserRelationshipController(UserRepository userRepository, RelationshipService relationshipService) {
        this.userRepository = userRepository;
        this.relationshipService = relationshipService;
    }

    @RequestMapping("/followings")
    public List<RelatedUserDTO> followings(@PathVariable("userId") long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        return relationshipService.findFollowings(user, pageParams);
    }

    @RequestMapping("/followers")
    public List<RelatedUserDTO> followers(@PathVariable("userId") long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        return relationshipService.findFollowers(user, pageParams);
    }

}
