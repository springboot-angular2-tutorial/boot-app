package com.myapp.controller;

import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
public class UserRelationshipController {

    private final RelationshipService relationshipService;

    @Autowired
    public UserRelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/followings")
    public List<RelatedUserDTO> followings(@PathVariable("userId") long userId, PageParams pageParams) {
        return relationshipService.findFollowings(userId, pageParams);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/followers")
    public List<RelatedUserDTO> followers(@PathVariable("userId") long userId, PageParams pageParams) {
        return relationshipService.findFollowers(userId, pageParams);
    }

}
