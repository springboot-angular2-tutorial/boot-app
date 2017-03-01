package com.myapp.dto.page

interface Page<out T> {
    val totalPages: Int
    val content: List<T>
}