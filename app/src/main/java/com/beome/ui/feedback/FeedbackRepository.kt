package com.beome.ui.feedback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.FeedbackPostUser
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FeedbackRepository(private val scope: CoroutineScope) {
    val addDataUserState = MutableLiveData<NetworkState>()
    val addDataFeedbackValueState = MutableLiveData<NetworkState>()
    fun getDetailPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

    fun getFeedbackComponent(): CollectionReference {
        return Firebase.firestore.collection("feedback_component")
    }

    fun getFeedbackUsers(idPost: String): CollectionReference {
        return Firebase.firestore.collection("feedback_post/$idPost/feedback_post_user")
    }

    fun getUsersBadge(): CollectionReference {
        return Firebase.firestore.collection("user")
    }

    fun addUserFeedback(idPost: String, feedback: FeedbackPostUser) {
        scope.launch {
            withContext(Dispatchers.IO) {
                Firebase.firestore.runTransaction { transaction ->
                    addDataUserState.postValue(NetworkState.LOADING)

                    val docPostRef = Firebase.firestore.collection("post").document(idPost)
                    val reportedPostRef =
                        Firebase.firestore.collection("reported_post").document(idPost)
                    //userRef and reportedUser
                    val docUserRef = Firebase.firestore.collection("user").document(feedback.authKey)
                    val docReportedUserRef = Firebase.firestore.collection("reported_account").document(feedback.authKey)
                    val reportedPost = transaction.get(reportedPostRef)
                    val post = transaction.get(docPostRef)
                    val user = transaction.get(docUserRef)
                    //set user point
                    val reportedUser = transaction.get(docReportedUserRef)
                    //increment feedbackCount
                    val counterFeedback = post["feedbackCount"] as Long + 1
                    //increment user point
                    val counterPoint = user["userPoint"] as Long + 5
                    if(reportedUser.exists()){
                        val counterPointReported = reportedUser["user.userPoint"] as Long + 5
                        transaction.update(docReportedUserRef, "user.userPoint", counterPointReported)
                    }
                    //update user point
                    transaction.update(docUserRef, "userPoint", counterPoint)
                    //update feedback count
                    transaction.update(docPostRef, "feedbackCount", counterFeedback)
                    if(reportedPost.exists()){
                        val counterFeedbackReported = reportedPost["post.feedbackCount"] as Long + 1
                        transaction.update(
                            reportedPostRef,
                            "post.feedbackCount",
                            counterFeedbackReported
                        )
                    }
                }.addOnFailureListener {
                    Log.d("err_add_feedback", it.message.toString())
                    addDataUserState.postValue(NetworkState.FAILED)
                }.addOnSuccessListener {
                    addDataUserState.postValue(NetworkState.SUCCESS)
                }.await()
            }
        }
    }

}