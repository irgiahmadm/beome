package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedbackPostUserValue(var componentName : String = "", var componentValue : Int = 0) : Parcelable