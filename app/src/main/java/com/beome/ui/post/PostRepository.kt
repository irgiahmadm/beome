package com.beome.ui.post

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.LikedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
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


    fun likePost(idPost : String, likedBy: String){
        scope.launch {
            try {
                Firebase.firestore.runTransaction{transaction ->
                    likePostState.postValue(NetworkState.LOADING)
                    Log.d("idPost", idPost)
                    val docLikedPostRef = Firebase.firestore.collection("post").document(idPost)
                    val post = transaction.get(docLikedPostRef)
                    val newLiked = (post.get("likeCount") as Long?)?.plus(1)
                    Log.d("likecount", newLiked.toString())
                    transaction.update(docLikedPostRef, "likedBy", FieldValue.arrayUnion(likedBy))
                    transaction.update(docLikedPostRef, "likeCount", newLiked)
                    likePostState.postValue(NetworkState.SUCCESS)
                }.await()
            }catch (e : Exception){
                likePostState.postValue(NetworkState.FAILED)
                Log.d("error_like_post", e.message.toString())
            }
        }
    }

    fun unlikePost(idPost : String, likedBy: String){
        try {
            Firebase.firestore.runTransaction { transaction ->
                likePostState.postValue(NetworkState.LOADING)
                val docLikedPostRef = Firebase.firestore.collection("post").document(idPost)
                val post = transaction.get(docLikedPostRef)
                val newLiked = (post.get("likeCount") as Long?)?.minus(1)
                Log.d("likecount", newLiked.toString())
                transaction.update(docLikedPostRef, "likedBy", FieldValue.arrayRemove(likedBy))
                transaction.update(docLikedPostRef, "likeCount", newLiked)
                likePostState.postValue(NetworkState.SUCCESS)
            }
        }catch (e : Exception){
            Log.d("error_unlike_post", e.message.toString())
            likePostState.postValue(NetworkState.FAILED)
        }
    }
    fun getLikedPost() : CollectionReference{
        return Firebase.firestore.collection("liked_post")
    }

    fun getListPost() : CollectionReference{
        return Firebase.firestore.collection("post")
    }
}