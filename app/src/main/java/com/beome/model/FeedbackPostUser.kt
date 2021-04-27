package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class FeedbackPostUser(
    var authKey: String = "",
    var idPost : String = "",
    var idFeedback : String = "",
    var username: String = "",
    var photoProfile: String? = "",
    var comment: String = "",
    var createdAt : Date = Date(),
    var status : Int = 1,
    var feedbackValue : @RawValue List<FeedbackPostUserValue> = arrayListOf()
) : Parcelable