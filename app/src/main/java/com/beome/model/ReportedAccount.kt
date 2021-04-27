package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportedAccount(var user : User = User(), var counter : Int = 0) : Parcelable