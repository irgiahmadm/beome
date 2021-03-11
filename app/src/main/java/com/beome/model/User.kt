package com.beome.model

data class User(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
    val birthDate: String,
    val following : Int,
    val post : Int,
    val authKey: String,
    val userStatus : Int,
    val createdAt: String,
    val updatedAt: String
)