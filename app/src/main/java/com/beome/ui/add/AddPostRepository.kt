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

class AddPostRepository(coroutineContext: CoroutineContext) {
    private val collectionFeedbackComponentRef = Firebase.firestore.collection("feedback_component")
    val addPostState = MutableLiveData<NetworkState>()
    val addComponentState = MutableLiveData<NetworkState>()
    private val job = Job()
    private val scope = CoroutineScope(coroutineContext + job)
    fun addPost(post: Post) {
        scope.launch {
            val collectionPostRef = Firebase.firestore.collection("post").document(post.idPost)
            addPostState.postValue(NetworkState.LOADING)
            collectionPostRef.set(post).await()
            //feedback repository
            Firebase.firestore.runTransaction { transaction ->
                val docUserRef = Firebase.firestore.collection("user").document(post.authKey)
                val docReportedUserRef =
                    Firebase.firestore.collection("reported_account").document(post.authKey)
                val user = transaction.get(docUserRef)
                val reportedUser = transaction.get(docReportedUserRef)
                //counter post
                val counterPost = user["post"] as Long + 1
                //counter point
                val counterPoint = user["userPoint"] as Long + 3
                if (reportedUser.exists()) {
                    val reportedCounter = reportedUser["user.post"] as Long + 1
                    val reportedUserCounterPoint = reportedUser["user.userPoint"] as Long + 3
                    transaction.update(docReportedUserRef, "user.post", reportedCounter)
                    transaction.update(
                        docReportedUserRef,
                        "user.userPoint",
                        reportedUserCounterPoint
                    )
                }
                transaction.update(docUserRef, "post", counterPost)
                transaction.update(docUserRef, "userPoint", counterPoint)
                //update feedback user point

            }.addOnFailureListener {
                addPostState.postValue(NetworkState.FAILED)
                Log.d("error_add_post", it.message!!)
            }.await()
            addPostState.postValue(NetworkState.SUCCESS)
        }
    }

    fun addComponentFeedbackPost(
        componentFeedbackPost: ComponentFeedbackPost,
        listSize: Int,
        counter: Int
    ) {
        scope.launch {
            try {
                addComponentState.postValue(NetworkState.LOADING)
                collectionFeedbackComponentRef.add(componentFeedbackPost).await()
                if (listSize == counter) {
                    addComponentState.postValue(NetworkState.SUCCESS)
                }
            } catch (e: Exception) {
                addComponentState.postValue(NetworkState.FAILED)
                Log.d("error_add_fdbck_cmpnnt", e.localizedMessage!!)
            }
        }

    }
}