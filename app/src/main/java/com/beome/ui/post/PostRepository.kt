package com.beome.ui.post

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.LikedPost
import com.beome.model.Post
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class PostRepository(private val scope: CoroutineScope) {
    val likePostState = MutableLiveData<NetworkState>()
    val editPostState = MutableLiveData<NetworkState>()
    val deletePostState = MutableLiveData<NetworkState>()

    fun likePost(idPost : String, likedBy: String){
        scope.launch {
            try {
                Firebase.firestore.runTransaction{transaction ->
                    likePostState.postValue(NetworkState.LOADING)
                    Log.d("idPost", idPost)
                    val docLikedPostRef = Firebase.firestore.collection("post").document(idPost)
                    val reportedPostRef = Firebase.firestore.collection("reported_post").document(idPost)
                    val post = transaction.get(docLikedPostRef)
                    val reportedPost = transaction.get(reportedPostRef)
                    val likeCount = (post.get("likeCount") as Long?)?.plus(1)
                    val likeCountReport = (reportedPost.get("likeCount") as Long?)?.plus(1)
                    transaction.update(docLikedPostRef, "likedBy", FieldValue.arrayUnion(likedBy))
                    transaction.update(docLikedPostRef, "likeCount", likeCount)
                    if(reportedPost.exists()){
                        transaction.update(reportedPostRef, "likedBy", FieldValue.arrayUnion(likedBy))
                        transaction.update(reportedPostRef, "likeCount", likeCountReport)
                    }
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
                val reportedPostRef = Firebase.firestore.collection("reported_post").document(idPost)
                val post = transaction.get(docLikedPostRef)
                val reportedPost = transaction.get(reportedPostRef)
                val likeCount = (post.get("likeCount") as Long?)?.minus(1)
                val likeCountReported = (reportedPost.get("likeCount") as Long?)?.minus(1)
                transaction.update(docLikedPostRef, "likedBy", FieldValue.arrayRemove(likedBy))
                transaction.update(docLikedPostRef, "likeCount", likeCount)
                if(reportedPost.exists()){
                    transaction.update(reportedPostRef, "likedBy", FieldValue.arrayRemove(likedBy))
                    transaction.update(reportedPostRef, "likeCount", likeCountReported)
                }
                likePostState.postValue(NetworkState.SUCCESS)
            }
        }catch (e : Exception){
            Log.d("error_unlike_post", e.message.toString())
            likePostState.postValue(NetworkState.FAILED)
        }
    }

    fun getListPost() : CollectionReference{
        return Firebase.firestore.collection("post")
    }

    fun updatePost(idPost : String, title : String, description : String){
        scope.launch {
            withContext(Dispatchers.IO){
                try {
                    editPostState.postValue(NetworkState.LOADING)
                    val docPostRef = Firebase.firestore.collection("post").document(idPost)
                    val data = hashMapOf("title" to title, "description" to description)
                    docPostRef.set(data, SetOptions.merge()).await()
                    val reportDetail = Firebase.firestore.collection("reported_post_detail").document(idPost)
                    reportDetail.set(data, SetOptions.merge()).await()
                    editPostState.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_update_post", "updatePost: ${e.message}")
                    editPostState.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    fun deletePost(idPost: String){
        scope.launch {
            withContext(Dispatchers.IO){
                try {
                    deletePostState.postValue(NetworkState.LOADING)
                    val docPostRef = Firebase.firestore.collection("post").document(idPost)
                    docPostRef.update("status", 0).await()
                    deletePostState.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_delete_post", "deletePost: ${e.message}")
                    deletePostState.postValue(NetworkState.FAILED)
                }
            }
        }
    }

}