package com.beome.ui.add

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.ComponentFeedbackPost
import com.beome.model.Post
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class AddPostRepository(coroutineContext : CoroutineContext) {
    private val collectionFeedbackComponentRef = Firebase.firestore.collection("feedback_component")
    val addPostState = MutableLiveData<NetworkState>()
    val addComponentState = MutableLiveData<NetworkState>()
    private val job= Job()
    private val scope = CoroutineScope(coroutineContext+job)
    fun addPost(post : Post){
        scope.launch {
            try {
                val collectionPostRef = Firebase.firestore.collection("post").document(post.idPost)
                addPostState.postValue(NetworkState.LOADING)
                collectionPostRef.set(post).await()
                Firebase.firestore.runTransaction { transaction ->
                    val docRef = Firebase.firestore.collection("user").document(post.authKey)
                    val user = transaction.get(docRef)
                    val counter = user["post"] as Long + 1
                    transaction.update(docRef, "post", counter)
                }.await()
                addPostState.postValue(NetworkState.SUCCESS)
            }catch (e : Exception){
                addPostState.postValue(NetworkState.FAILED)
                Log.d("error_add_post", e.localizedMessage!!)
            }
        }
    }

    fun addComponentFeedbackPost(componentFeedbackPost: ComponentFeedbackPost, listSize: Int, counter : Int){
        scope.launch {
            try {
                addComponentState.postValue(NetworkState.LOADING)
                collectionFeedbackComponentRef.add(componentFeedbackPost).await()
                if(listSize == counter){
                    addComponentState.postValue(NetworkState.SUCCESS)
                }
            }catch (e : Exception){
                addComponentState.postValue(NetworkState.FAILED)
                Log.d("error_add_fdbck_cmpnnt", e.localizedMessage!!)
            }
        }

    }
}