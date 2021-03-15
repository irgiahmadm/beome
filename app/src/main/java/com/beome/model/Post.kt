package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var idPost: String = "",
    var authKey : String = "",
    var username: String = "",
    var imgUser: String? = "",
    var imagePost: String = "",
    var title: String = "",
    var description: String = "",
    var likeCount: Int = 0,
    var feedbackCount : Int = 0,
    var status : Int = 1,
    var createdAt: String = "",
    var updatedAt: String  = ""
) : Parcelable