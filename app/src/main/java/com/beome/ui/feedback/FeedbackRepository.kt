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

    fun getFeedbackUser(idPost:String, idUser: String) : CollectionReference{
        return Firebase.firestore.collection("feedback_post/$idPost/$idUser")
    }

    fun getFeedbackDocument(idPost : String) : DocumentReference{
        return Firebase.firestore.collection("feedback_post").document(idPost)
    }

    fun getListFeedbackPost(idPost: String): DocumentReference {
        return Firebase.firestore.collection("feedback_post").document(idPost)
    }

    fun getFeedbackValue(idPost:String, idUser: String, idFeedbackPost : String) : CollectionReference{
        return Firebase.firestore.collection("feedback_post/$idPost/$idUser/$idFeedbackPost/feedback_component_post")
    }

    fun addUsertoFeedback(idPost:String, idUser: String, user : FeedbackPostUser, idFeedbackPost: String){
        val userFeedbackPostRef = Firebase.firestore
            .collection("feedback_post")
            .document(idPost)
            .collection("feedback_post_user")
            .document(idUser)
        scope.launch {
            try {
                addDataUserState.postValue(NetworkState.LOADING)
                userFeedbackPostRef.set(user).await()
                addDataUserState.postValue(NetworkState.SUCCESS)
            }catch (e : Exception){
                Log.d("error_add_user_to_fdbck", e.localizedMessage!!)
                addDataUserState.postValue(NetworkState.FAILED)
            }
        }
    }

    fun addFeedbackValue(idPost:String, idUser: String, feedbackValue : FeedbackPostUserValue, listSize : Int, counter : Int, idFeedbackPost : String){
        val userFeedbackPostRef = Firebase.firestore
            .collection("feedback_post")
            .document(idPost)
            .collection("feedback_post_user")
            .document(idUser)
            .collection("feedback_post_value")
            .document(idFeedbackPost)
        scope.launch {
            try {
                addDataFeedbackValueState.postValue(NetworkState.LOADING)
                userFeedbackPostRef.set(feedbackValue).await()
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