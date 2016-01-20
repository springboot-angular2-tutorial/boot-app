package com.myapp.controller;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.repository.MicropostRepository;
import com.myapp.service.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final MicropostRepository micropostRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public FeedController(MicropostRepository micropostRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.securityContextService = securityContextService;
    }

    @RequestMapping
    public List<Micropost> feed(@RequestParam("sinceId") Optional<Long> sinceId,
                                @RequestParam("maxId") Optional<Long> maxId,
                                @RequestParam("count") Optional<Integer> count) {
        User currentUser = securityContextService.currentUser();
        return micropostRepository
                .findAsFeed(currentUser, sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));
    }

}
