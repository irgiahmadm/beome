package com.beome.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.Follow
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ProfileRepository(private val coroutineScope: CoroutineScope) {
    val profileState = MutableLiveData<NetworkState>()
    private val followRef = Firebase.firestore.collection("follow")


    fun getUserProfile() : CollectionReference{
        return Firebase.firestore.collection("user")
    }
    fun getPostByUser(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

    fun followUser(follow : Follow){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    Firebase.firestore.runTransaction {transaction ->
                        val userRef = Firebase.firestore.collection("user").document(follow.followedId)
                        val user = transaction.get(userRef)
                        val newFollower = user["follower"] as Long + 1
                        transaction.update(userRef,"follower", newFollower)
                        null
                    }.await()
                    followRef.add(follow)
                }catch (e : Exception){
                    Log.d("error_follow", e.localizedMessage!!)
                }
            }
        }
    }

    fun getFollowStatus() : CollectionReference{
        return followRef
    }
}