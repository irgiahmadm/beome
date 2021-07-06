package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
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
    var role : Int = 0,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var userPoint : Int = 0,
    var token : String = ""
) : Parcelable