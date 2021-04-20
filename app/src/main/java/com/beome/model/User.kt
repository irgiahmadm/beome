package com.beome.model

import java.util.*

data class User(
    var photoProfile : String = "",
    var fullName: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var birthDate: String = "",
    var follower : Int = 0,
    var post : Int = 0,
    var authKey: String = "",
    var userStatus : Int = 0,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date()
)