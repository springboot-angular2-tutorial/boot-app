package com.myapp.controller;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.myapp.domain.QUser.user;
import static com.querydsl.core.types.dsl.Wildcard.count;

@RestController
@RequestMapping("/api/users/{userId}/microposts")
public class UserMicropostController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final UserRepository userRepository;
    private final MicropostRepository micropostRepository;

    @Autowired
    public UserMicropostController(UserRepository userRepository, MicropostRepository micropostRepository) {
        this.userRepository = userRepository;
        this.micropostRepository = micropostRepository;
    }

    @RequestMapping
    public List<Micropost> list(@PathVariable("userId") Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        return micropostRepository.findByUser(user, pageParams);
    }
}
