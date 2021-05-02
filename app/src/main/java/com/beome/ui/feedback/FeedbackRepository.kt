package com.beome.ui.feedback

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.FeedbackPostUser
import com.beome.model.FeedbackPostUserValue
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.Dispatcher
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

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

    fun addUserFeedback(idPost: String, feedback: FeedbackPostUser) {
        val userFeedbackPostRef = Firebase.firestore
            .collection("feedback_post")
            .document(idPost)
            .collection("feedback_post_user")
            .document(feedback.idFeedback)
        userFeedbackPostRef.set(feedback)
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Firebase.firestore.runTransaction { transaction ->
                        addDataUserState.postValue(NetworkState.LOADING)
                        val docPostRef = Firebase.firestore.collection("post").document(idPost)
                        val reportedPostRef = Firebase.firestore.collection("reported_post").document(idPost)
                        val reportedPost = transaction.get(reportedPostRef)
                        val post = transaction.get(docPostRef)
                        val counterFeedback = post["feedbackCount"] as Long + 1
                        val counterFeedbackReported = reportedPost["post.feedbackCount"] as Long + 1

                        transaction.update(docPostRef, "feedbackCount", counterFeedback)
                        transaction.update(reportedPostRef, "post.feedbackCount", counterFeedbackReported)
                        addDataUserState.postValue(NetworkState.SUCCESS)
                    }
                } catch (e: Exception) {
                    Log.d("error_add_user_to_fdbck", e.localizedMessage!!)
                    addDataUserState.postValue(NetworkState.FAILED)
                }
            }

        }
    }
}