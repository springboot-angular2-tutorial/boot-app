package com.myapp.service

interface RelationshipService {
    fun follow(userId: Long)
    fun unfollow(userId: Long)
}