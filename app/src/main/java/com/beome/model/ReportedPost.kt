package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportedPost(var post : Post = Post(), var counter : Int = 0) : Parcelable