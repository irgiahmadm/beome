package com.beome.utilities

import android.app.Activity
import android.net.Uri
import com.yalantis.ucrop.UCrop

object UcropHelper {
    fun openCropActivity(sourceUri: Uri, destinationUri: Uri, activity : Activity) {
        val options = UCrop.Options()
        options.setHideBottomControls(true)
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .start(activity)
    }
}