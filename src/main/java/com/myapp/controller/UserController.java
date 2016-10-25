package com.myapp.controller;

import com.myapp.auth.TokenHandler;
import com.myapp.domain.User;
import com.myapp.dto.ErrorResponse;
import com.myapp.dto.UserDTO;
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

import javax.annotation.Nullable;
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
    public Page<UserDTO> list(@RequestParam(value = "page", required = false) @Nullable Integer page,
                              @RequestParam(value = "size", required = false) @Nullable Integer size) {
        final PageRequest pageable = new PageRequest(
                Optional.ofNullable(page).orElse(1) - 1,
                Optional.ofNullable(size).orElse(DEFAULT_PAGE_SIZE));
        return userService.findAll(pageable);
    }

    @RequestMapping(method = RequestMethod.POST)
    public User create(@Valid @RequestBody UserParams params) {
        return userRepository.save(params.toUser());
    }

    @RequestMapping(value = "{id:\\d+}")
    public UserDTO show(@PathVariable("id") Long id) throws UserNotFoundException {
        return userService.findOne(id).orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping("/me")
    public UserDTO showMe() throws UserNotFoundException {
        return userService.findMe().orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "/me", method = RequestMethod.PATCH)
    public ResponseEntity updateMe(@Valid @RequestBody UserParams params) {
        User user = securityContextService.currentUser();
        userService.update(user, params);

        // when username was changed, re-issue jwt.
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-auth-token", tokenHandler.createTokenForUser(user));

        return new ResponseEntity(headers, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleValidationException(DataIntegrityViolationException e) {
        return new ErrorResponse("email_already_taken", "This email is already taken.");
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No user")
    private class UserNotFoundException extends Exception {
    }

}
