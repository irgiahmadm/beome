package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class Post(
    var idPost: String = "",
    var authKey : String = "",
    var username: String = "",
    var imgUser: String? = "",
    var imagePost: String = "",
    var title: String = "",
    var description: String= "",
    var likeCount: Int = 0,
    var feedbackCount : Int = 0,
    var likedBy : @RawValue List<String> = arrayListOf(),
    var status : Int = 1,
    var createdAt: Date = Date(),
    var updatedAt: Date  = Date()
) : Parcelable