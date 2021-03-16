package com.beome.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ComponentFeedbackPost(
    var idComponentFeedbackPost: String?=null,
    var idPost: String?=null,
    var componentName: String?=null
) : Parcelable