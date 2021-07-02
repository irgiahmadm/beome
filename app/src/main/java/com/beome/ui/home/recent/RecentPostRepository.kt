package com.beome.ui.home.recent

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecentPostRepository {

    fun getRecentPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

}