package com.beome.utilities

import android.app.Activity
import android.net.Uri
import com.yalantis.ucrop.UCrop

object UcropHelper {
    fun openCropActivity(sourceUri: Uri, destinationUri: Uri, activity : Activity) {
        UCrop.of(sourceUri, destinationUri)
            .start(activity)
    }
}