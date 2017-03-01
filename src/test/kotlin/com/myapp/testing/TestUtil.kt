package com.myapp.testing

import com.myapp.domain.Micropost
import com.myapp.domain.RelatedUser
import com.myapp.domain.User

val TestUser = User(
    _id = 1,
    username = "test@test.com",
    password = "encrypted password",
    name = "John Doe"
)
val TestMicropost = Micropost(
    _id = 1,
    content = "test content",
    user = TestUser
)
val TestRelatedUser = RelatedUser(
    _id = 1,
    username = "test@test.com",
    name = "John Doe",
    relationshipId = 1
)

