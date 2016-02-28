package com.myapp.controller;

import com.myapp.dto.PostDTO;
import com.myapp.service.MicropostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final MicropostService micropostService;

    @Autowired
    public FeedController(MicropostService micropostService) {
        this.micropostService = micropostService;
    }

    @RequestMapping
    public List<PostDTO> feed(@RequestParam("sinceId") Optional<Long> sinceId,
                              @RequestParam("maxId") Optional<Long> maxId,
                              @RequestParam("count") Optional<Integer> count) {
        return micropostService.findAsFeed(sinceId, maxId, count.orElse(DEFAULT_PAGE_SIZE));

    }

}
