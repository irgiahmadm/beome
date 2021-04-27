package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportedFeedback (var feedback : FeedbackPostUser = FeedbackPostUser(), var counter : Int = 0):Parcelable