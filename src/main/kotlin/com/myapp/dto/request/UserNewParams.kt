package com.myapp.dto.request

import javax.validation.constraints.Size

data class UserNewParams(
    val email: String,
    @get:Size(min = 8, max = 10)
    val password: String,
    val name: String
)

