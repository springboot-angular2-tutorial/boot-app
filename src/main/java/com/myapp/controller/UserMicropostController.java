package com.myapp.controller;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.repository.UserRepository;
import com.myapp.service.MicropostService;
import com.myapp.service.SecurityContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserMicropostController {

    private final UserRepository userRepository;
    private final MicropostService micropostService;
    private final SecurityContextService securityContextService;

    @Autowired
    public UserMicropostController(UserRepository userRepository, MicropostService micropostService, SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.micropostService = micropostService;
        this.securityContextService = securityContextService;
    }

    @RequestMapping("/{userId:\\d+}/microposts")
    public List<PostDTO> list(@PathVariable("userId") Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        return micropostService.findByUser(user, pageParams);
    }

    @RequestMapping("/me/microposts")
    public List<PostDTO> list(PageParams pageParams) {
        final User user = securityContextService.currentUser();
        return micropostService.findByUser(user, pageParams);
    }

}
