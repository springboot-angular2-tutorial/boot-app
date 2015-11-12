package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.domain.Micropost;
import com.myapp.repository.MicropostRepository;
import com.myapp.service.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final MicropostRepository micropostRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public FeedController(MicropostRepository micropostRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.securityContextService = securityContextService;
    }

    @RequestMapping
    public Page<Micropost> feed() {
        User currentUser = securityContextService.currentUser();
        return micropostRepository.findAsFeed(currentUser, new PageRequest(0, 5));
    }

}
