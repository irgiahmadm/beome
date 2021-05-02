package com.beome.ui.admin.feedback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportedFeedback
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReportedFeedbackRepository(private val scope : CoroutineScope) {
    val stateReportedFeedback = MutableLiveData<NetworkState>()
    private val listReportedFeedback = MutableLiveData<List<ReportedFeedback>>()
    private val reportedFeedback = MutableLiveData<ReportedFeedback>()
    val networkStateTakedownPost = MutableLiveData<NetworkState>()

    fun getListReportedFeedback() : LiveData<List<ReportedFeedback>> {
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedFeedback = Firebase.firestore.collection("reported_feedback")
                refReportedFeedback.addSnapshotListener { value, error ->
                    stateReportedFeedback.postValue(NetworkState.LOADING)
                    val tempReportedPost = mutableListOf<ReportedFeedback>()
                    value?.let {
                        if(it.documents.isEmpty()){
                            stateReportedFeedback.postValue(NetworkState.NOT_FOUND)
                        }else{
                            for (document in it){
                                val reportedPost = document.toObject<ReportedFeedback>()
                                tempReportedPost.add(reportedPost)
                            }
                            listReportedFeedback.value = tempReportedPost
                            stateReportedFeedback.postValue(NetworkState.SUCCESS)
                        }
                    }
                    error?.let {
                        Log.d("err_get_rprtd_fbck", it.message.toString())
                        stateReportedFeedback.postValue(NetworkState.FAILED)
                    }
                }
            }
        }
        return listReportedFeedback
    }

    fun getReportedFeedback(idFeedback : String) : LiveData<ReportedFeedback>{
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedPost = Firebase.firestore.collection("reported_feedback").document(idFeedback)
                refReportedPost.addSnapshotListener { value, error ->
                    stateReportedFeedback.postValue(NetworkState.LOADING)
                    value?.let {
                        if(it.exists()){
                            val feedback = it.toObject<ReportedFeedback>()
                            reportedFeedback.value = feedback!!
                            stateReportedFeedback.postValue(NetworkState.SUCCESS)
                        }else{
                            stateReportedFeedback.postValue(NetworkState.NOT_FOUND)
                        }
                    }
                    error?.let {
                        Log.d("err_get_rprtd_fdbck", it.message.toString())
                        stateReportedFeedback.postValue(NetworkState.FAILED)
                    }
                }
            }
        }
        return reportedFeedback
    }

    fun takedownFeedback(idFeedback: String, idPost : String){
        scope.launch {
            withContext(Dispatchers.IO){
                val refFeedback = Firebase.firestore.collection("feedback_post/$idPost/feedback_post_user").document(idFeedback)
                val refReportedFeedback = Firebase.firestore.collection("reported_feedback").document(idFeedback)
                networkStateTakedownPost.postValue(NetworkState.LOADING)
                Firebase.firestore.runTransaction { transaction ->
                    transaction.update(refFeedback, "status", 2)
                    transaction.update(refReportedFeedback, "feedback.status", 2)
                }.addOnFailureListener {
                    Log.d("err_takedown_fdbck", it.toString())
                    networkStateTakedownPost.postValue(NetworkState.FAILED)
                }.addOnSuccessListener {
                    networkStateTakedownPost.postValue(NetworkState.SUCCESS)
                }.await()
            }
        }
    }
}