package com.beome.ui.search

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchRepository {
    fun getListPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }
    fun getListUser(): CollectionReference {
        return Firebase.firestore.collection("user")
    }
}