package com.myapp.domain

import com.fasterxml.jackson.annotation.JsonIgnore

interface HasIdentity<out T> {

    @get:JsonIgnore
    val _id: T?

    val id: T
        get() = _id ?: throw RuntimeException("This model is not saved yet.")

}