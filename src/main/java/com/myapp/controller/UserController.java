package com.myapp.controller;

import com.myapp.auth.TokenHandler;
import com.myapp.domain.User;
import com.myapp.dto.ErrorResponse;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserOptionalParams;
import com.myapp.dto.UserParams;
import com.myapp.repository.UserRepository;
import com.myapp.service.SecurityContextService;
import com.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Integer DEFAULT_PAGE_SIZE = 5;

    private final UserRepository userRepository;
    private final UserService userService;
    private final SecurityContextService securityContextService;
    private final TokenHandler tokenHandler;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, SecurityContextService securityContextService, TokenHandler tokenHandler) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.securityContextService = securityContextService;
        this.tokenHandler = tokenHandler;
    }

    @RequestMapping
    public Page<User> list(@RequestParam(value = "page") Optional<Integer> page,
                           @RequestParam(value = "size") Optional<Integer> size) {
        return userRepository.findAll(new PageRequest(page.orElse(1) - 1, size.orElse(DEFAULT_PAGE_SIZE)));
    }

    @RequestMapping(method = RequestMethod.POST)
    public User create(@Valid @RequestBody UserParams params) {
        return userRepository.save(params.toUser());
    }

    @RequestMapping(value = "{id:\\d+}")
    public UserDTO show(@PathVariable("id") Long id) {
        User currentUser = securityContextService.currentUser();
        return userRepository.findOne(id, currentUser);
    }

    @RequestMapping("/me")
    public UserDTO showMe() {
        User user = securityContextService.currentUser();
        return userRepository.findOne(user.getId(), user);
    }

    @RequestMapping(value = "/me", method = RequestMethod.PATCH)
    public ResponseEntity updateMe(@Valid @RequestBody UserParams params) {
        User user = securityContextService.currentUser();
        UserOptionalParams optionalParams = params.toOptionalParams();
        userService.update(user, optionalParams);

        // when username was changed, re-issue jwt.
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-TOKEN", tokenHandler.createTokenForUser(user));

        return new ResponseEntity(headers, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleValidationException(DataIntegrityViolationException e) {
        return new ErrorResponse("email_already_taken", "This email is already taken.");
    }
}
