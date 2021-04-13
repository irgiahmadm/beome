package com.beome.ui.home.following

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowingPostRepository {

    fun getFollowing() : CollectionReference{
        return Firebase.firestore.collection("follow")
    }
}