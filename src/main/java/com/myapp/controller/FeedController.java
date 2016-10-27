package com.myapp.controller;

import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.service.MicropostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private final MicropostService micropostService;

    @Autowired
    public FeedController(MicropostService micropostService) {
        this.micropostService = micropostService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<PostDTO> feed(PageParams pageParams) {
        return micropostService.findAsFeed(pageParams);
    }

}
