package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.UserRepository;
import com.myapp.domain.Micropost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}/microposts")
public class UserMicropostController {

    private static final Integer DEFAULT_PAGE_SIZE = 5;

    private final UserRepository userRepository;
    private final MicropostRepository micropostRepository;

    @Autowired
    public UserMicropostController(UserRepository userRepository, MicropostRepository micropostRepository) {
        this.userRepository = userRepository;
        this.micropostRepository = micropostRepository;
    }

    @RequestMapping
    public Page<Micropost> list(@PathVariable("userId") Long userId,
                                @RequestParam(value = "page") Optional<Integer> page,
                                @RequestParam(value = "size") Optional<Integer> size) {
        User user = userRepository.findOne(userId);
        Pageable pageRequest = new PageRequest(page.orElse(1) - 1,
                size.orElse(DEFAULT_PAGE_SIZE), Sort.Direction.DESC, "createdAt");

        return micropostRepository.findByUser(user, pageRequest);
    }
}
