package com.beome.ui.post

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.LikedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class PostRepository(coroutineContext: CoroutineContext) {
    private val likePostState = MutableLiveData<NetworkState>()
    private val job= Job()
    private val scope = CoroutineScope(coroutineContext+job)
    private val collectionLikedPostRed = Firebase.firestore.collection("liked_post")

    fun likePost(likedPost: LikedPost){
        scope.launch {
            try {
                likePostState.postValue(NetworkState.LOADING)
                collectionLikedPostRed.add(likedPost).await()
                likePostState.postValue(NetworkState.SUCCESS)
            }catch (e : Exception){
                likePostState.postValue(NetworkState.FAILED)
                Log.d("error_add_post", e.localizedMessage!!)
            }
        }
    }
    fun getLikedPost() : CollectionReference{
        return Firebase.firestore.collection("liked_post")
    }

    fun getListLikedPost() : CollectionReference{
        return Firebase.firestore.collection("post")
    }
}