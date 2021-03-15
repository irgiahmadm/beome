package com.beome.ui.feedback

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedbackRepository {
    fun getDetailPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

}