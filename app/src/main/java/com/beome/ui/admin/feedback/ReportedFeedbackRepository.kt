package com.beome.ui.admin.feedback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportedAccount
import com.beome.model.ReportedFeedback
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportedFeedbackRepository(private val scope : CoroutineScope) {
    val stateReportedFeedback = MutableLiveData<NetworkState>()
    private val listReportedFeedback = MutableLiveData<List<ReportedFeedback>>()
    private val reportedFeedback = MutableLiveData<ReportedFeedback>()

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
                val refReportedAccount = Firebase.firestore.collection("reported_feedback").document(idFeedback)
                refReportedAccount.addSnapshotListener { value, error ->
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
}