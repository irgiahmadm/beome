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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class FeedbackRepository(coroutineContext: CoroutineContext) {
    val addDataUserState = MutableLiveData<NetworkState>()
    val addDataFeedbackValueState = MutableLiveData<NetworkState>()
    private val job= Job()
    private val scope = CoroutineScope(coroutineContext+job)
    fun getDetailPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

    fun getFeedbackComponent(): CollectionReference{
        return Firebase.firestore.collection("feedback_component")
    }

    fun getListFeedbackUser(idPost: String): CollectionReference {
        return Firebase.firestore.collection("feedback_post/$idPost/feedback_post_user")
    }

    fun addUserFeedback(idPost:String, idUser: String, feedback : FeedbackPostUser){
        val userFeedbackPostRef = Firebase.firestore
            .collection("feedback_post")
            .document(idPost)
            .collection("feedback_post_user")
            .document(idUser)
        scope.launch {
            try {
                addDataUserState.postValue(NetworkState.LOADING)
                userFeedbackPostRef.set(feedback).await()
                addDataUserState.postValue(NetworkState.SUCCESS)

            }catch (e : Exception){
                Log.d("error_add_user_to_fdbck", e.localizedMessage!!)
                addDataUserState.postValue(NetworkState.FAILED)
            }
        }
    }

    fun addFeedbackValue(idPost:String, idUser: String, feedbackValue : FeedbackPostUserValue, listSize : Int, counter : Int){
        val userFeedbackPostRef = Firebase.firestore
            .collection("feedback_post")
            .document(idPost)
            .collection("feedback_post_user")
            .document(idUser)
            .collection("feedback_post_value_$idUser")
            .document()
        scope.launch {
            try {
                addDataFeedbackValueState.postValue(NetworkState.LOADING)
                userFeedbackPostRef.set(feedbackValue).await()
                Log.d("feedback_size", "$listSize $counter")
                if(listSize == counter){
                    addDataFeedbackValueState.postValue(NetworkState.SUCCESS)
                }
            }catch (e : Exception){
                Log.d("error_add_val_to_fdbck", e.localizedMessage!!)
                addDataFeedbackValueState.postValue(NetworkState.FAILED)
            }
        }
    }
}