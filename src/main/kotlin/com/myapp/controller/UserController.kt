package com.myapp.controller

import com.myapp.domain.User
import com.myapp.dto.page.Page
import com.myapp.dto.request.UserEditParams
import com.myapp.dto.request.UserNewParams
import com.myapp.dto.response.ErrorResponse
import com.myapp.repository.exception.EmailDuplicatedException
import com.myapp.repository.exception.RecordNotFoundException
import com.myapp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun list(
        @RequestParam(value = "page", required = false) page: Int?,
        @RequestParam(value = "size", required = false) size: Int?
    ): Page<User> {
        return userService.findAll(
            page = page ?: 1,
            size = size ?: 5
        )
    }

    @PostMapping
    fun create(@Valid @RequestBody params: UserNewParams): User {
        return userService.create(params)
    }

    @GetMapping(path = arrayOf("{id:\\d+}"))
    fun show(@PathVariable("id") id: Long) = userService.findOne(id)

    @GetMapping(path = arrayOf("/me"))
    fun showMe() = userService.findMe()

    @PatchMapping(path = arrayOf("/me"))
    fun updateMe(@Valid @RequestBody params: UserEditParams) = userService.updateMe(params)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailDuplicatedException::class)
    fun handleEmailDuplicatedException(e: EmailDuplicatedException): ErrorResponse {
        return ErrorResponse("email_already_taken", "This email is already taken.")
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No user")
    @ExceptionHandler(RecordNotFoundException::class)
    fun handleUserNotFound() {
    }

}

